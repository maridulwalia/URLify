package com.urlify.util;

import org.springframework.stereotype.Component;

/**
 * Base62 encoder for converting numeric IDs to short alphanumeric codes.
 * Uses characters: 0-9, a-z, A-Z (62 total characters)
 * Time Complexity: O(log n) where n is the input number
 */
@Component
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    /**
     * Encode a long number to Base62 string
     * 
     * @param num The number to encode
     * @return Base62 encoded string
     */
    public String encode(long num) {
        if (num == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % BASE);
            sb.append(BASE62_CHARS.charAt(remainder));
            num /= BASE;
        }

        return sb.reverse().toString();
    }

    /**
     * Decode a Base62 string to long number
     * 
     * @param str The Base62 string to decode
     * @return Decoded number
     */
    public long decode(String str) {
        long num = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int index = BASE62_CHARS.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            num = num * BASE + index;
        }
        return num;
    }
}
