package filters;

import models.Media;
import models.Subject;
import java.util.List;
import java.util.stream.Collectors;

public class SubjectFilter implements MediaFilter {
    private Subject subject;

    public SubjectFilter(Subject subject) {
        this.subject = subject;
    }

    @Override
    public List<Media> filter(List<Media> mediaList) {
        if (subject == null) return mediaList;
        return mediaList.stream()
            .filter(m -> m.getSubjects().contains(subject))
            .collect(Collectors.toList());
    }
}
