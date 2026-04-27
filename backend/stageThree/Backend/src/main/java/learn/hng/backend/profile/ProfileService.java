package learn.hng.backend.profile;

import learn.hng.backend.dto.ProfileMapper;
import learn.hng.backend.dto.responses.CreateProfileResponse;
import learn.hng.backend.dto.responses.ProfileDTO;
import learn.hng.backend.enrichment.EnrichmentService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EnrichmentService enrichmentService;
    private final ProfileMapper profileMapper;

    public ProfileService(ProfileRepository profileRepository, EnrichmentService enrichmentService, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.enrichmentService = enrichmentService;
        this.profileMapper = profileMapper;
    }

    public List<Profile> getAll() {
        return profileRepository.findAll();
    }

    // Returns the profile + a flag indicating whether it was just created
//    public record CreateResult(ProfileDTO profile, boolean isNew) {}

    public CreateProfileResponse createOrGet(String name) throws SQLException, IOException, InterruptedException {
        String normalized = name.trim().toLowerCase();

        // Idempotency check — return existing if found
        Optional<Profile> existing = profileRepository.findByName(normalized);
        if (existing.isPresent()) {
            return new CreateProfileResponse("success", profileMapper.toDto(existing.get()));
        }

        // Enrich via external APIs and persist
        Profile profile = profileRepository.save(enrichmentService.enrich(normalized));
        return new CreateProfileResponse("success", profileMapper.toDto(profile));
    }

//    public CreateProfileResponse create(String name ) throws IOException, InterruptedException {
//        ProfileDTO response = enrichmentService.enrich(name);
//        return new CreateProfileResponse("success", response);
//    }

}