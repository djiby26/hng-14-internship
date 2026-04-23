package com.hng.service;

import com.hng.dao.ProfileDao;
import com.hng.model.Profile;
import com.hng.model.ProfileSummary;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProfileService {

    private final ProfileDao  dao;
    private final EnrichmentService enrichment;

    public ProfileService(ProfileDao dao, EnrichmentService enrichment) {
        this.dao        = dao;
        this.enrichment = enrichment;
    }

    // Returns the profile + a flag indicating whether it was just created
    public record CreateResult(Profile profile, boolean isNew) {}

    public CreateResult createOrGet(String name) throws SQLException, IOException, InterruptedException {
        String normalized = name.trim().toLowerCase();

        // Idempotency check — return existing if found
        Optional<Profile> existing = dao.findByName(normalized);
        if (existing.isPresent()) {
            return new CreateResult(existing.get(), false);
        }

        // Enrich via external APIs and persist
        Profile profile = enrichment.enrich(normalized);
        dao.create(profile);
        return new CreateResult(profile, true);
    }

    public Optional<Profile> getById(String id) throws SQLException {
        return dao.findById(UUID.fromString(id));
    }

    public List<Profile> getAll(String gender, String countryId,
                                       String ageGroup, String minAge, String maxAge,
                                       String minGenderProbability, String minCountryProbability, String sortBy, String order, String page, String limit
    ) throws SQLException
    {
        return dao.findAll(gender, countryId, ageGroup, minAge, maxAge, minGenderProbability, minCountryProbability, sortBy, order, page, limit);
    }

    public int getTotalProfileCount() throws SQLException {
        return dao.countAll();
    }

    public boolean delete(String id) throws SQLException {
        return dao.deleteById(UUID.fromString(id));
    }
}