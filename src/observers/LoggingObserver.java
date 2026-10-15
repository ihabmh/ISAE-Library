package observers;

import models.Media;

// Simple observer that logs new media additions to the console
public class LoggingObserver implements Observer {
    @Override
    public void update(Media media) {
        System.out.println("LOG: New media added -> " + media.getTitle());
    }
}
