/*
 * Copyright (c) 2015, Simon Morgan
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package io.sjm.diary;

import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a diary entry. Diary entries are stored on disk in JSON text files. Each diary entry
 * is password encrypted.
 */
public class Entry implements JSONString {
    private LocalDate date;
    private String text = "";

    private String filename;

    public Entry() {
        this.date = LocalDate.now();
        this.filename = getFilename();
    }

    public Entry(LocalDate date) throws IOException {
        this.date = date;
        this.filename = getFilename();

        if (exists())
            text = load();
        else
            text = "";
    }

    public String getText() {
        assert text != null;

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override public String toJSONString() {
        assert date != null;
        assert text != null;

        JSONStringer stringer = new JSONStringer();

        stringer.object();
        stringer.key("date");
        stringer.value(String.valueOf(date));
        stringer.key("entry");
        stringer.value(text);
        stringer.endObject();

        return stringer.toString();
    }

    /**
     * Read a diary entry from the corresponding text file.
     *
     * @return a string representing this diary entry
     * @throws IOException
     */
    public String load() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        String str = new String(encoded, Charset.defaultCharset());

        JSONObject obj = new JSONObject(str);

        date = LocalDate.parse(obj.getString("date"));
        text = obj.getString("entry");

        return text;
    }

    public void save() throws IOException {
        assert filename != null;

        Path filePath = Paths.get(filename);
        Files.createDirectories(filePath.getParent());
        Files.write(Paths.get(filename), toJSONString().getBytes(), StandardOpenOption.CREATE);
    }

    public boolean exists() {
        assert filename != null;

        return Files.exists(Paths.get(filename));
    }

    /**
     * Generates the path to the file used to store the diary entry.
     *
     * @return the path to the diary entry
     */
    private String getFilename() {
        assert date != null;

        String dirName = date.format(DateTimeFormatter.ofPattern(Settings.fmt));
        String fileName =
            date.format(DateTimeFormatter.ofPattern(Settings.fmt.replace(Settings.sep, "-")));
        String tmp = Settings.homeDir + dirName + File.separator + fileName + ".json";

        System.out.println(tmp);

        return tmp;
    }
}
