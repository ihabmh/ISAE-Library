package observers;

import models.Media;

// Interface for objects that can be observed (Subject in the Observer pattern)
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(Media media);
}
