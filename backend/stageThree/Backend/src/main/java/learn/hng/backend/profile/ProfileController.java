package learn.hng.backend.profile;

import learn.hng.backend.dto.responses.CreateProfileResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("api/profiles")
public class ProfileController {
    ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/")
    public List<Profile> getProfiles() {
        return this.profileService.getAll();
    }

    @PostMapping
    public CreateProfileResponse create(@RequestParam String name) throws IOException, InterruptedException, SQLException {
        return profileService.createOrGet(name);
    }
}
