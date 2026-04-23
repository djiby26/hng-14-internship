package com.hng.parser;

import java.util.Map;

public class QueryParser {

    // Known country name → ISO code mappings
    private static final Map<String, String> COUNTRY_MAP = Map.ofEntries(
            Map.entry("nigeria",      "NG"),
            Map.entry("kenya",        "KE"),
            Map.entry("tanzania",     "TZ"),
            Map.entry("angola",       "AO"),
            Map.entry("ghana",        "GH"),
            Map.entry("ethiopia",     "ET"),
            Map.entry("uganda",       "UG"),
            Map.entry("senegal",      "SN"),
            Map.entry("cameroon",     "CM"),
            Map.entry("egypt",        "EG")
            // add more as needed
    );

    private static final Map<String, String> AGE_GROUP_MAP = Map.of(
            "child",      "child",
            "children",   "child",
            "teenager",   "teenager",
            "teenagers",  "teenager",
            "teen",       "teenager",
            "teens",      "teenager",
            "adult",      "adult",
            "adults",     "adult",
            "senior",     "senior",
            "seniors",    "senior"
    );

    // Result holder
    public static class ParsedQuery {
        public String gender;
        public String countryId;
        public String ageGroup;
        public String minAge;
        public String maxAge;
        public boolean interpreted = false;
    }

    public static ParsedQuery parse(String q) {
        ParsedQuery result = new ParsedQuery();

        if (q == null || q.isBlank()) return result;

        String input = q.toLowerCase().trim();

        // --- GENDER ---
        if (input.contains("female") || input.contains("females") || input.contains("women") || input.contains("woman")) {
            result.gender = "female";
        } else if (input.contains("male") || input.contains("males") || input.contains("men") || input.contains("man")) {
            result.gender = "male";
        }

        // --- COUNTRY ---
        for (Map.Entry<String, String> entry : COUNTRY_MAP.entrySet()) {
            if (input.contains(entry.getKey())) {
                result.countryId = entry.getValue();
                break;
            }
        }

        // --- AGE GROUP (explicit) ---
        for (Map.Entry<String, String> entry : AGE_GROUP_MAP.entrySet()) {
            if (input.contains(entry.getKey())) {
                result.ageGroup = entry.getValue();
                break;
            }
        }

        // --- YOUNG → age 16–24 (overrides age group) ---
        if (input.contains("young") && !input.contains("younger")) {
            result.minAge = "16";
            result.maxAge = "24";
            result.ageGroup = null; // "young" is not a stored group
        }

        // --- "above X" / "over X" ---
        java.util.regex.Matcher aboveMatcher = java.util.regex.Pattern
                .compile("(?:above|over)\\s+(\\d+)")
                .matcher(input);
        if (aboveMatcher.find()) {
            result.minAge = aboveMatcher.group(1);
        }

        // --- "below X" / "under X" ---
        java.util.regex.Matcher belowMatcher = java.util.regex.Pattern
                .compile("(?:below|under)\\s+(\\d+)")
                .matcher(input);
        if (belowMatcher.find()) {
            result.maxAge = belowMatcher.group(1);
        }

        // --- "between X and Y" ---
        java.util.regex.Matcher betweenMatcher = java.util.regex.Pattern
                .compile("between\\s+(\\d+)\\s+and\\s+(\\d+)")
                .matcher(input);
        if (betweenMatcher.find()) {
            result.minAge = betweenMatcher.group(1);
            result.maxAge = betweenMatcher.group(2);
        }

        // Mark as interpreted if at least one filter was extracted
        result.interpreted = result.gender != null
                || result.countryId != null
                || result.ageGroup != null
                || result.minAge != null
                || result.maxAge != null;

        return result;
    }
}
