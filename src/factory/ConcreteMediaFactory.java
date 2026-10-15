package factory;

import models.Media;
import java.util.HashMap;
import java.util.Map;

// Concrete implementation of MediaFactory using the Registry pattern
public class ConcreteMediaFactory implements MediaFactory {
    private Map<String, MediaCreator> creators = new HashMap<>();

    // Register a specific creator for a media type
    public void registerCreator(String type, MediaCreator creator) {
        creators.put(type, creator);
    }

    @Override
    public Media createMedia(String type, Map<String, Object> attributes) {
        MediaCreator creator = creators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown media type: " + type);
        }
        return creator.create(attributes);
    }
}
