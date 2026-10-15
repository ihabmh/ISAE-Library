package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Abstract base class for all media types
public abstract class Media implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String author;
    private int publicationYear;
    private String description;
    private int accessCount;
    private List<Subject> subjects;

    public Media(String id, String title, String author, int publicationYear, String description) {
        // Generate a random ID if none is provided
        this.id = (id == null || id.isEmpty()) ? UUID.randomUUID().toString().substring(0, 8) : id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.description = description;
        this.accessCount = 0;
        this.subjects = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPublicationYear() { return publicationYear; }
    public String getDescription() { return description; }
    public int getAccessCount() { return accessCount; }
    public List<Subject> getSubjects() { return subjects; }

    // Abstract methods to be implemented by subclasses
    public abstract void afficher();
    public abstract String getDisplayMedia();
    public abstract String getType();

    // Increment the number of times this media has been accessed
    public void incrementerAcces() { this.accessCount++; }
    
    // Add a subject to this media
    public void addSubject(Subject subject) { this.subjects.add(subject); }
}
