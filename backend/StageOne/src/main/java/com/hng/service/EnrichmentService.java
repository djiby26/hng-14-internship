package com.hng.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hng.dto.ExternalApiResponses;
import com.hng.exceptions.ExternalApiException;
import com.hng.model.Profile;
import com.hng.dto.ExternalApiResponses.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;

public class EnrichmentService {

    private final HttpClient   http;
    private final ObjectMapper mapper;

    public EnrichmentService() {
        this.http   = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    // -------------------------------------------------------------------------
    // Main entry — calls all 3 APIs and builds the profile
    // -------------------------------------------------------------------------

    public Profile enrich(String name) throws IOException, InterruptedException {
        ExternalApiResponses.GenderizeResponse genderize  = fetchGenderize(name);
        ExternalApiResponses.AgifyResponse agify = fetchAgify(name);
        ExternalApiResponses.NationalizeResponse nationalize = fetchNationalize(name);

        // Validate each API response before proceeding
        if (genderize.gender == null || genderize.count == 0) {
            throw new ExternalApiException("Genderize");
        }
        if (agify.age == null) {
            throw new ExternalApiException("Agify");
        }
        if (nationalize.country == null || nationalize.country.isEmpty()) {
            throw new ExternalApiException("Nationalize");
        }

        String topCountryId  = pickTopCountry(nationalize.country);
        double topCountryProb = pickTopProbability(nationalize.country);

        return Profile.create(
            name,
            genderize.gender,
            genderize.probability,
            genderize.count,                    // renamed from count → sample_size
            agify.age   != null ? agify.age : 0,
            classifyAgeGroup(agify.age),
            topCountryId,
            topCountryProb
        );
    }

    // -------------------------------------------------------------------------
    // Individual API calls
    // -------------------------------------------------------------------------

    private ExternalApiResponses.GenderizeResponse fetchGenderize(String name) throws IOException, InterruptedException {
        String body = get("https://api.genderize.io?name=" + name);
        return mapper.readValue(body, ExternalApiResponses.GenderizeResponse.class);
    }

    private AgifyResponse fetchAgify(String name) throws IOException, InterruptedException {
        String body = get("https://api.agify.io?name=" + name);
        return mapper.readValue(body, AgifyResponse.class);
    }

    private NationalizeResponse fetchNationalize(String name) throws IOException, InterruptedException {
        String body = get("https://api.nationalize.io?name=" + name);
        return mapper.readValue(body, NationalizeResponse.class);
    }

    private String get(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String pickTopCountry(List<NationalizeResponse.CountryEntry> countries) {
        if (countries == null || countries.isEmpty()) return "UNKNOWN";
        return countries.stream()
            .max(Comparator.comparingDouble(c -> c.probability))
            .get().countryId;
    }

    private double pickTopProbability(List<NationalizeResponse.CountryEntry> countries) {
        if (countries == null || countries.isEmpty()) return 0.0;
        double raw = countries.stream()
            .max(Comparator.comparingDouble(c -> c.probability))
            .get().probability;
        return Math.round(raw * 100.0) / 100.0;  // 2 decimal places e.g. 0.85
    }

    private String classifyAgeGroup(Integer age) {
        if (age == null)  return "unknown";
        if (age <= 12)    return "child";
        if (age <= 19)    return "teenager";
        if (age <= 59)    return "adult";
        return "senior";
    }
}
