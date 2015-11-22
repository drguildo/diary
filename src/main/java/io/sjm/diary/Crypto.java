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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;

/**
 * A simple helper class for encryption and decryption.
 */
public class Crypto {
    private final static String CIPHER = "Blowfish";
    private final static String CHAR_ENCODING = "UTF-8";

    /**
     * Encrypt the given string using Blowfish and returns the Base64 encoded result.
     *
     * @param plainText     the text to encode
     * @param encryptionKey the encryption key
     * @return the Bases64 encoded result of encrypting the plaintext
     * @throws GeneralSecurityException
     */
    public static String encryptString(String plainText, String encryptionKey)
        throws GeneralSecurityException {
        byte[] encrypted;

        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey.getBytes(), CIPHER);
        Cipher cipherInstance = Cipher.getInstance(CIPHER);

        cipherInstance.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        encrypted = cipherInstance.doFinal(plainText.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts the given Base64 encoded Blowfish ciphertext.
     *
     * @param cipherText    the text to decode
     * @param encryptionKey the decryption key
     * @return the Base64 decoded result of decrypting the ciphertext
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String decryptString(String cipherText, String encryptionKey)
        throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher cipherInstance = Cipher.getInstance(CIPHER);
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(CHAR_ENCODING), CIPHER);

        cipherInstance.init(Cipher.DECRYPT_MODE, key);

        return new String(cipherInstance.doFinal(Base64.getDecoder().decode(cipherText)),
            CHAR_ENCODING);
    }
}
