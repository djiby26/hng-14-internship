package com.hng.dao;

import com.hng.db.Connexion;
import com.hng.model.Profile;
import com.hng.model.ProfileSummary;
import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProfileDao {

    public ProfileDao(){

    }

    public void create(Profile profile) throws SQLException {
        String sql = """
                INSERT INTO profiles
                    (id, name, gender, gender_prob, sample_size,
                     age, age_group, country_id, country_prob, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getId().toString());
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getGender());
            stmt.setDouble(4, profile.getGenderProbability());
            stmt.setInt   (5, profile.getSampleSize());
            stmt.setInt   (6, profile.getAge());
            stmt.setString(7, profile.getAgeGroup());
            stmt.setString(8, profile.getCountryId());
            stmt.setDouble(9, profile.getCountryProbability());
            stmt.setTimestamp(10, Timestamp.from(profile.getCreatedAt()));

            stmt.executeUpdate();
        }
    }

    public Optional<Profile> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM profiles WHERE name = ? LIMIT 1";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFull(rs));
                }
                return Optional.empty();
            }
        }
    }

    public Optional<Profile> findById(UUID id) throws SQLException {
        String sql = "SELECT * FROM profiles WHERE id = ?";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFull(rs));
                }
                return Optional.empty();
            }
        }
    }

    public List<ProfileSummary> findAll(String gender, String countryId, String ageGroup) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT id, name, gender, age, age_group, country_id FROM profiles"
        );

        List<String> conditions = new ArrayList<>();
        List<String> values     = new ArrayList<>();

        if (gender != null && !gender.isBlank()) {
            conditions.add("gender = ?");
            values.add(gender.toLowerCase());
        }
        if (countryId != null && !countryId.isBlank()) {
            conditions.add("country_id = ?");
            values.add(countryId.toUpperCase());
        }
        if (ageGroup != null && !ageGroup.isBlank()) {
            conditions.add("age_group = ?");
            values.add(ageGroup.toLowerCase());
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        sql.append(" ORDER BY created_at DESC");

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())){

            for (int i = 0; i < values.size(); i++) {
                stmt.setString(i + 1, values.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                List<ProfileSummary> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapSummary(rs));
                }

            return results;
            }
        }
    }


    public boolean deleteById(UUID id) throws SQLException {
        String sql = "DELETE FROM profiles WHERE id = ?";

        try (Connection conn = Connexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.toString());
            return stmt.executeUpdate() > 0;   // 0 = not found, 1 = deleted
        }
    }

    private Profile mapFull(ResultSet rs) throws SQLException {
        Profile p = new Profile();
        p.setId               (UUID.fromString(rs.getString("id")));
        p.setName             (rs.getString("name"));
        p.setGender           (rs.getString("gender"));
        p.setGenderProbability(rs.getDouble("gender_prob"));
        p.setSampleSize       (rs.getInt("sample_size"));
        p.setAge              (rs.getInt("age"));
        p.setAgeGroup         (rs.getString("age_group"));
        p.setCountryId        (rs.getString("country_id"));
        p.setCountryProbability(rs.getDouble("country_prob"));
        p.setCreatedAt        (rs.getTimestamp("created_at").toInstant());
        return p;
    }

    private ProfileSummary mapSummary(ResultSet rs) throws SQLException {
        return new ProfileSummary(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("gender"),
                rs.getInt("age"),
                rs.getString("age_group"),
                rs.getString("country_id")
        );
    }
}