package com.hng.model;
import java.util.UUID;

public class ProfileSummary {

    private UUID   id;
    private String name;
    private String gender;
    private int    age;
    private String ageGroup;
    private String countryId;

    public ProfileSummary() {}

    public ProfileSummary(UUID id, String name, String gender,
                                int age, String ageGroup, String countryId) {
        this.id        = id;
        this.name      = name;
        this.gender    = gender;
        this.age       = age;
        this.ageGroup  = ageGroup;
        this.countryId = countryId;
    }

    public UUID   getId()        { return id; }
    public String getName()      { return name; }
    public String getGender()    { return gender; }
    public int    getAge()       { return age; }
    public String getAgeGroup()  { return ageGroup; }
    public String getCountryId() { return countryId; }
}

