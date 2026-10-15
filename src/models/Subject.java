package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Represents a study subject or course module
public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private Specialty specialty;
    private List<Media> mediaList;

    public Subject(String code, Specialty specialty) {
        this.code = code;
        this.specialty = specialty;
        this.mediaList = new ArrayList<>();
    }

    public String getCode() { return code; }
    public Specialty getSpecialty() { return specialty; }
    public List<Media> getMediaList() { return mediaList; }
    
    // Links a media item to this subject
    public void addMedia(Media media) { this.mediaList.add(media); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return code.equals(subject.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
