package manager;

import filters.MediaFilter;
import models.Media;
import models.Specialty;
import models.Student;
import observers.Observer;
import observers.Subject;

import java.util.ArrayList;
import java.util.List;

// Singleton class that manages the library's data and observers
public class LibraryManager implements Subject {
    private static LibraryManager instance;
    private List<Media> allMedia;
    private List<Student> students;
    private List<Specialty> specialties;
    private List<Observer> observers;

    private LibraryManager() {
        this.allMedia = new ArrayList<>();
        this.students = new ArrayList<>();
        this.specialties = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    // Get the singleton instance
    public static synchronized LibraryManager getInstance() {
        if (instance == null) {
            instance = new LibraryManager();
        }
        return instance;
    }

    // Media management
    public void addMedia(Media media) {
        allMedia.add(media);
        notifyObservers(media);
    }

    public void removeMedia(Media media) {
        allMedia.remove(media);
    }

    public List<Media> getAllMedia() { return allMedia; }

    // Student management
    public List<Student> getStudents() { return students; }
    
    public void addStudent(Student student) {
        students.add(student);
        attach(student);
    }

    // Specialty management
    public List<Specialty> getSpecialties() { return specialties; }
    
    public void addSpecialty(Specialty specialty) {
        specialties.add(specialty);
    }

    // Observers management
    public List<Observer> getObservers() { return observers; }

    // Filter media using a specific filter strategy
    public List<Media> filterMedia(MediaFilter filter) {
        return filter.filter(allMedia);
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Media media) {
        for (Observer observer : observers) {
            observer.update(media);
        }
    }

    // Setters
    public void setAllMedia(List<Media> mediaList) { this.allMedia = mediaList; }
    public void setStudents(List<Student> studentList) { this.students = studentList; }
    public void setSpecialties(List<Specialty> specialtyList) { this.specialties = specialtyList; }
}
