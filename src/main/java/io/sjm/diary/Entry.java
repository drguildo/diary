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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a diary entry. Diary entries are stored on disk in JSON text files. Each diary entry
 * is password encrypted.
 */
public class Entry implements JSONString {
    protected LocalDate date;
    protected String text = "";

    protected String password;
    protected String filename;

    public Entry(String password) {
        this.date = LocalDate.now();
        this.filename = getFilename();
        this.password = password;
    }

    public Entry(String password, LocalDate date) throws IOException {
        this.date = date;
        this.filename = getFilename();
        this.password = password;

        if (exists())
            load(password);
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
     * @throws IOException
     */
    private void load(String password) throws IOException {
        try {
            String ciphertext = Utils.load(Paths.get(filename));
            String plaintext = Crypto.decryptString(ciphertext, password);
            fromJSONString(plaintext);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs the diary entry from a JSON encoded string.
     *
     * @param s a JSON encoded string representing a diary entry
     */
    private void fromJSONString(final String s) {
        JSONObject obj = new JSONObject(s);

        date = LocalDate.parse(obj.getString("date"));
        text = obj.getString("entry");
    }

    public void save(String password) throws IOException {
        assert filename != null;

        try {
            String ciphertext = Crypto.encryptString(toJSONString(), password);
            Path filePath = Paths.get(filename);
            Files.createDirectories(filePath.getParent());
            Files.write(Paths.get(filename), ciphertext.getBytes(), StandardOpenOption.CREATE);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private boolean exists() {
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
