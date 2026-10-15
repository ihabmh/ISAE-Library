package models;

// Represents an interactive online quiz
public class OnlineQuiz extends Media {
    private static final long serialVersionUID = 1L;
    private int estimatedDuration; // in minutes
    private String difficultyLevel;

    public OnlineQuiz(String id, String title, String author, int publicationYear, String description, int estimatedDuration, String difficultyLevel) {
        super(id, title, author, publicationYear, description);
        this.estimatedDuration = estimatedDuration;
        this.difficultyLevel = difficultyLevel;
    }

    public int getEstimatedDuration() { return estimatedDuration; }
    public String getDifficultyLevel() { return difficultyLevel; }

    @Override
    public void afficher() {
        System.out.println("Online Quiz: " + getTitle() + " by " + getAuthor() + " (Difficulty: " + difficultyLevel + ")");
    }

    @Override
    public String getDisplayMedia() {
        return "Estimated duration: " + estimatedDuration + " min, Difficulty level: " + difficultyLevel;
    }

    @Override
    public String getType() {
        return "OnlineQuiz";
    }
}
