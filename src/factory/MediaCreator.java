package factory;

import models.Media;
import java.util.Map;

public interface MediaCreator {
    Media create(Map<String, Object> attributes);
}
