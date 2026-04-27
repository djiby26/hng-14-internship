package learn.hng.backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class NationalizeResponse {
    public String name;
    public List<CountryEntry> country;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountryEntry {
        @JsonProperty("country_id")
        public String countryId;
        public double probability;
    }
}
