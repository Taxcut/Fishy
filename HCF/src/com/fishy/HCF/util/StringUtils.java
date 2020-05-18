package com.fishy.hcf.util;

public class StringUtils {

    public static String stringBuilder(String[] message, int startingPoint, boolean trim) {
        StringBuilder sb = new StringBuilder();

        for (int i = startingPoint; i < message.length; i++) {
            sb.append(message[i]).append(" ");
        }

        return trim ? sb.toString().trim() : sb.toString();
    }
}
