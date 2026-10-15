package filters;

import models.Media;
import models.Specialty;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialtyFilter implements MediaFilter {
    private Specialty specialty;

    public SpecialtyFilter(Specialty specialty) {
        this.specialty = specialty;
    }

    @Override
    public List<Media> filter(List<Media> mediaList) {
        if (specialty == null) return mediaList;
        return mediaList.stream()
            .filter(m -> m.getSubjects().stream()
                .anyMatch(s -> s.getSpecialty().equals(specialty)))
            .collect(Collectors.toList());
    }
}
