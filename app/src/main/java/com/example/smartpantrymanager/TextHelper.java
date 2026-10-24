package com.example.smartpantrymanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TextHelper {

    private static final Set<String> KEEP_AS_IS = new HashSet<>(Arrays.asList(
            "hummus", "couscous", "asparagus", "molasses", "citrus", "swiss"));

    private static final Map<String, String> IRREGULAR = new HashMap<>();
    static {
        IRREGULAR.put("leaves", "leaf");
        IRREGULAR.put("loaves", "loaf");
        IRREGULAR.put("knives", "knife");
        IRREGULAR.put("halves", "half");
    }

    private TextHelper() {}

    public static String normalize(String name) {
        if (name == null) return "";
        String cleaned = name.trim().toLowerCase().replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) return "";

        StringBuilder result = new StringBuilder();
        for (String word : cleaned.split(" ")) {
            if (result.length() > 0) result.append(' ');
            result.append(singular(word));
        }
        return result.toString();
    }

    private static String singular(String word) {
        if (KEEP_AS_IS.contains(word)) return word;
        if (IRREGULAR.containsKey(word)) return IRREGULAR.get(word);

        if (word.length() > 3 && word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("oes")) {
            return word.substring(0, word.length() - 2);
        }
        if (word.length() > 2 && word.endsWith("s")
                && !word.endsWith("ss") && !word.endsWith("us") && !word.endsWith("is")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}