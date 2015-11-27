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
import org.json.JSONStringer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String toJSONString(final Entry entry) {
        JSONStringer stringer = new JSONStringer();

        stringer.object();
        stringer.key("date");
        stringer.value(String.valueOf(entry.getDate()));
        stringer.key("entry");
        stringer.value(entry.getText());
        stringer.endObject();

        return stringer.toString();
    }

    /**
     * Read a diary entry from the corresponding file.
     *
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static Entry loadEntry(final LocalDate date)
        throws IOException, GeneralSecurityException {
        assert !Settings.PASSWORD.isEmpty();

        byte[] encoded = Files.readAllBytes(getPath(date));
        String ciphertext = new String(encoded, Charset.defaultCharset());
        String plaintext = Crypto.decryptString(ciphertext, Settings.PASSWORD);

        JSONObject json = new JSONObject(plaintext);

        return new Entry(LocalDate.parse(json.getString("date")), json.getString("entry"));
    }

    public static void saveEntry(final Entry entry) throws IOException {
        assert !Settings.PASSWORD.isEmpty();

        Path path = getPath(entry.getDate());

        try {
            String ciphertext = Crypto.encryptString(toJSONString(entry), Settings.PASSWORD);
            Files.createDirectories(path.getParent());
            Files.write(path, ciphertext.getBytes(), StandardOpenOption.CREATE);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether a file corresponding to the given date exists.
     *
     * @param date the date to be checked for a corresponding file
     * @return whether a file exists for the specified date
     */
    public static boolean entryExists(final LocalDate date) {
        return Files.exists(getPath(date));
    }

    /**
     * Given a date, generate a file path for entry loading and saving.
     *
     * @param date the date of the relevant diary entry
     * @return the path to the diary entry
     */
    private static Path getPath(final LocalDate date) {
        String dirName = date.format(DateTimeFormatter.ofPattern(Settings.DIRFORMAT));
        String filename = date.format(DateTimeFormatter.ofPattern(Settings.FILEFORMAT));

        return Paths.get(Settings.homeDir + dirName + File.separator + filename + ".json");
    }
}
