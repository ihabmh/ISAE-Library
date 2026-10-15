package filters;

import models.Media;
import java.util.List;
import java.util.stream.Collectors;

public class TitleFilter implements MediaFilter {
    private String title;

    public TitleFilter(String title) {
        this.title = title;
    }

    @Override
    public List<Media> filter(List<Media> mediaList) {
        if (title == null || title.isEmpty()) return mediaList;
        return mediaList.stream()
            .filter(m -> m.getTitle().toLowerCase().contains(title.toLowerCase()))
            .collect(Collectors.toList());
    }
}
