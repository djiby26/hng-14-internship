package com.hng.model;

import com.github.f4b6a3.uuid.UuidCreator;
import java.time.Instant;
import java.util.UUID;

public class Profile {

    private String id;
    private String name;
    private String gender;
    private double genderProbability;
    private int age;
    private String ageGroup;
    private String countryId;
    private String countryName;
    private double countryProbability;
    private Instant createdAt;

    // Used when constructing a NEW record (before inserting into DB)
    public static Profile create(
            String name,
            String gender,
            double genderProbability,
            int age,
            String ageGroup,
            String countryId,
            String countryName,
            double countryProbability
    ) {
        Profile p = new Profile();
//        p.id = UuidCreator.getTimeOrderedEpoch(); // UUID v7
        p.name = name;
        p.gender = gender;
        p.genderProbability = genderProbability;
        p.age = age;
        p.ageGroup = ageGroup;
        p.countryId = countryId;
        p.countryName = countryName;
        p.countryProbability = countryProbability;
        p.createdAt = Instant.now();
        return p;
    }

    // Used when mapping a row FROM the database (DAO layer)
    public Profile() {}

    // Getters
    public String getId()                     { return id; }
    public String getName()                 { return name; }
    public String getGender()               { return gender; }
    public double getGenderProbability()    { return genderProbability; }
    public int getAge()                     { return age; }
    public String getAgeGroup()             { return ageGroup; }
    public String getCountryId()            { return countryId; }
    public String getCountryName()          { return countryName; }
    public double getCountryProbability()   { return countryProbability; }
    public Instant getCreatedAt()           { return createdAt; }

    // Setters (used by DAO when mapping ResultSet)
    public void setId(String id)                              { this.id = id; }
    public void setName(String name)                        { this.name = name; }
    public void setGender(String gender)                    { this.gender = gender; }
    public void setGenderProbability(double genderProbability) { this.genderProbability = genderProbability; }
    public void setAge(int age)                             { this.age = age; }
    public void setAgeGroup(String ageGroup)                { this.ageGroup = ageGroup; }
    public void setCountryId(String countryId)              { this.countryId = countryId; }
    public void setCountryName(String countryName)              { this.countryName = countryName; }
    public void setCountryProbability(double countryProbability) { this.countryProbability = countryProbability; }
    public void setCreatedAt(Instant createdAt)             { this.createdAt = createdAt; }
}
