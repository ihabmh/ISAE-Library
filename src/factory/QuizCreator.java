package factory;

import models.Media;
import models.OnlineQuiz;
import java.util.Map;

public class QuizCreator implements MediaCreator {
    @Override
    public Media create(Map<String, Object> attributes) {
        return new OnlineQuiz(
            (String) attributes.getOrDefault("id", ""),
            (String) attributes.get("title"),
            (String) attributes.get("author"),
            (int) attributes.get("publicationYear"),
            (String) attributes.get("description"),
            (int) attributes.get("estimatedDuration"),
            (String) attributes.get("difficultyLevel")
        );
    }
}
