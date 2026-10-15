package models;

// Represents a recorded video session
public class VideoSession extends Media {
    private static final long serialVersionUID = 1L;
    private int duration; // in minutes

    public VideoSession(String id, String title, String author, int publicationYear, String description, int duration) {
        super(id, title, author, publicationYear, description);
        this.duration = duration;
    }

    public int getDuration() { return duration; }

    @Override
    public void afficher() {
        System.out.println("Video Session: " + getTitle() + " by " + getAuthor() + " (" + duration + " minutes)");
    }

    @Override
    public String getDisplayMedia() {
        return "Duration: " + duration + " min";
    }

    @Override
    public String getType() {
        return "VideoSession";
    }
}
