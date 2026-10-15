package factory;

import models.Media;
import java.util.Map;

// Interface for media creation factories
public interface MediaFactory {
    Media createMedia(String type, Map<String, Object> attributes);
}
