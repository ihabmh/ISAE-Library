package models;

import observers.Observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Represents a student user of the library
public class Student implements Serializable, Observer {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private Specialty specialty;
    private List<Subject> subjects;

    public Student(String username, String password, Specialty specialty) {
        this.username = username;
        this.password = password;
        this.specialty = specialty;
        this.subjects = new ArrayList<>();
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Specialty getSpecialty() { return specialty; }
    public List<Subject> getSubjects() { return subjects; }

    // Add a subject to the student's interests
    public void addSubject(Subject subject) { this.subjects.add(subject); }

    // Check if the student is interested in a specific media based on its subjects
    public boolean isInterestedIn(Media media) {
        return media.getSubjects().stream().anyMatch(s -> this.subjects.contains(s));
    }

    // Implementation of the Observer pattern update method
    @Override
    public void update(Media media) {
        if (isInterestedIn(media)) {
            recevoirNotification("New media available in your subjects: " + media.getTitle());
        }
    }

    // Prints a notification message to the console
    public void recevoirNotification(String message) {
        System.out.println("[Notification for " + username + "] " + message);
    }
}
