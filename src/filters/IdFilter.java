package filters;

import models.Media;
import java.util.List;
import java.util.stream.Collectors;

public class IdFilter implements MediaFilter {
    private String id;

    public IdFilter(String id) {
        this.id = id;
    }

    @Override
    public List<Media> filter(List<Media> mediaList) {
        return mediaList.stream()
            .filter(m -> m.getId().equalsIgnoreCase(id))
            .collect(Collectors.toList());
    }
}
