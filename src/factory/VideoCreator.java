package factory;

import models.Media;
import models.VideoSession;
import java.util.Map;

public class VideoCreator implements MediaCreator {
    @Override
    public Media create(Map<String, Object> attributes) {
        return new VideoSession(
            (String) attributes.getOrDefault("id", ""),
            (String) attributes.get("title"),
            (String) attributes.get("author"),
            (int) attributes.get("publicationYear"),
            (String) attributes.get("description"),
            (int) attributes.get("duration")
        );
    }
}
