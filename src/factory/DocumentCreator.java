package factory;

import models.Document;
import models.Media;
import java.util.Map;

public class DocumentCreator implements MediaCreator {
    @Override
    public Media create(Map<String, Object> attributes) {
        return new Document(
            (String) attributes.getOrDefault("id", ""),
            (String) attributes.get("title"),
            (String) attributes.get("author"),
            (int) attributes.get("publicationYear"),
            (String) attributes.get("description"),
            (int) attributes.get("numberOfPages")
        );
    }
}
