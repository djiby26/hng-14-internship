package learn.hng.backend.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

public record PaginationResponse(
        String status,
        int page,
        int limit,
        long total,
        @JsonProperty("total_pages") int totalPages,
        Map<String, String> links,
        @Valid List<ProfileDTO> data
) {}
