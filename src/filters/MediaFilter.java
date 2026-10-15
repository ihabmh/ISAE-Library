package filters;

import models.Media;
import java.util.List;

// Interface for media filtering strategies
public interface MediaFilter {
    List<Media> filter(List<Media> mediaList);
}
