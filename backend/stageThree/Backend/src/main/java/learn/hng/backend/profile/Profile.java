package learn.hng.backend.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity(name = "profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Profile {

    public Profile() {}

    public Profile(String name, String gender, double genderProbability, int age, String ageGroup, String countryId, String countryName,double countryProbability) {
        this.name = name;
        this.gender = gender;
        this.genderProbability = genderProbability;
        this.age = age;
        this.ageGroup = ageGroup;
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryProbability = countryProbability;
//        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

//    @NotBlank
    private String name;

//    @NotBlank
    private String gender;

//    @NotBlank
    @JsonProperty("gender_probability")
    private double genderProbability;

//    @Min(value = 1)
    private int age;

//    @NotBlank
    @JsonProperty("age_group")
    private String ageGroup;

//    @NotBlank
    @JsonProperty("country_id")
    private String countryId;

//    @NotBlank
    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("country_probability")
    private double countryProbability;

//    @NotBlank
    @JsonProperty("created_at")
    private Instant createdAt;

}
