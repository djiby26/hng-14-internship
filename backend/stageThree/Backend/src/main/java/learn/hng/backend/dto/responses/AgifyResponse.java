package learn.hng.backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgifyResponse {
    public String name;
    public Integer age;  // nullable — API returns null if unknown
    public int count;
}