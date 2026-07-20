package com.example.demo.recommend.util;

public final class PackingIconMapper {

    private static final String DEFAULT_ICON_KEY = "u1F4E6";

    private PackingIconMapper() {
    }

    public static String findIconKey(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            return DEFAULT_ICON_KEY;
        }

        String normalizedName = itemName.replace(" ", "");

        if (containsAny(normalizedName, "선크림", "로션", "선블록")) {
            return "u1F9F4";
        }

        if (containsAny(normalizedName, "선글라스")) {
            return "u1F576";
        }

        if (containsAny(normalizedName, "챙모자", "밀짚모자")) {
            return "u1F452";
        }

        if (containsAny(normalizedName, "볼캡", "캡모자")) {
            return "u1F9E2";
        }

        if (containsAny(normalizedName, "양산")) {
            return "u2602";
        }

        if (containsAny(normalizedName, "부채", "휴대용선풍기")) {
            return "u1FAAD";
        }

        if (containsAny(normalizedName, "수영복", "래시가드")) {
            return "u1FA71";
        }

        if (containsAny(normalizedName, "비키니")) {
            return "u1FA59";
        }

        if (containsAny(normalizedName, "조리", "아쿠아슈즈", "슬리퍼")) {
            return "u1FA74";
        }

        if (containsAny(normalizedName, "물안경", "수경")) {
            return "u1F97D";
        }

        return DEFAULT_ICON_KEY;
    }

    private static boolean containsAny(String itemName, String... keywords) {
        for (String keyword : keywords) {
            if (itemName.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}