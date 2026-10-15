package observers;

import models.Media;

// Interface for objects that want to be notified of library updates
public interface Observer {
    void update(Media media);
}
