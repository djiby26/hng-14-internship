package learn.hng.backend.dto;

import learn.hng.backend.dto.responses.ProfileDTO;
import learn.hng.backend.profile.Profile;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfileMapper {

    /**
     * Maps a single Entity to a DTO
     */
    public ProfileDTO toDto(Profile entity) {
        if (entity == null) {
            return null;
        }

        return new ProfileDTO(
                entity.getId(),
                entity.getName(),
                entity.getGender(),
                entity.getGenderProbability(),
                0,
                entity.getAge(),
                entity.getAgeGroup(),
                entity.getCountryId(),
                entity.getCountryName(),
                entity.getCountryProbability(),
                entity.getCreatedAt()
        );
    }

    /**
     * Maps a list of Entities to a list of DTOs (Great for your "data": [...] field)
     */
    public List<ProfileDTO> toDtoList(List<Profile> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}