package models;

import java.io.Serializable;
import java.util.List;

public class LibraryData implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Media> allMedia;
    private List<Student> students;
    private List<Specialty> specialties;

    public LibraryData(List<Media> allMedia, List<Student> students, List<Specialty> specialties) {
        this.allMedia = allMedia;
        this.students = students;
        this.specialties = specialties;
    }

    public List<Media> getAllMedia() { return allMedia; }
    public List<Student> getStudents() { return students; }
    public List<Specialty> getSpecialties() { return specialties; }
}
