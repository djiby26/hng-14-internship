package learn.hng.backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenderizeResponse {
    public String name;
    public String gender;
    public double probability;
    public int count;
}