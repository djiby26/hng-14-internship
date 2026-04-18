package com.hng.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// --- Genderize ---
// {"count":1234,"gender":"female","name":"ella","probability":0.99}
public class ExternalApiResponses {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GenderizeResponse {
        public String name;
        public String gender;
        public double probability;
        public int count;
    }

    // --- Agify ---
    // {"age":46,"count":1234,"name":"ella"}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AgifyResponse {
        public String name;
        public Integer age;  // nullable — API returns null if unknown
        public int count;
    }

    // --- Nationalize ---
    // {"country":[{"country_id":"DK","probability":0.08}],"name":"ella"}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NationalizeResponse {
        public String name;
        public List<CountryEntry> country;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CountryEntry {
            @JsonProperty("country_id")
            public String countryId;
            public double probability;
        }
    }
}
