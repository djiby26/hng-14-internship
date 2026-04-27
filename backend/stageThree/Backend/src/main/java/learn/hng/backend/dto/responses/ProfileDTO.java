package learn.hng.backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record ProfileDTO(
        String id,
        String name,
        String gender,
        @JsonProperty("gender_probability")
        double genderProbability,
        int count,
        int age,
        @JsonProperty("age_group")
        String ageGroup,
        @JsonProperty("country_id")
        String countryId,
        @JsonProperty("country_name")
        String countryName,
        @JsonProperty("country_probability")
        double countryProbability,
        @JsonProperty("created_at")
        Instant createdAt
) {}
