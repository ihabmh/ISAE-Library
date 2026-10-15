package filters;

import models.Media;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorFilter implements MediaFilter {
    private String authorName;

    public AuthorFilter(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public List<Media> filter(List<Media> mediaList) {
        if (authorName == null || authorName.isEmpty()) return mediaList;
        return mediaList.stream()
            .filter(m -> m.getAuthor().equalsIgnoreCase(authorName))
            .collect(Collectors.toList());
    }
}
