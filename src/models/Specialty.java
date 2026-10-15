package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Represents an academic specialty or department
public class Specialty implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Subject> subjects;

    public Specialty(String name) {
        this.name = name;
        this.subjects = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<Subject> getSubjects() { return subjects; }
    
    // Add a subject to this specialty
    public void addSubject(Subject subject) { this.subjects.add(subject); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specialty specialty = (Specialty) o;
        return name.equals(specialty.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
