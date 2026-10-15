package observers;

import models.Media;
import java.util.ArrayList;
import java.util.List;

// Observer that triggers UI refreshes across the application
public class DataRefreshObserver implements Observer {
    private List<RefreshableUI> interfaces = new ArrayList<>();

    // Register a UI component to be refreshed
    public void addInterface(RefreshableUI ui) {
        interfaces.add(ui);
    }

    @Override
    public void update(Media media) {
        // Refresh all registered UI components
        for (RefreshableUI ui : interfaces) {
            ui.refreshDisplay();
        }
    }
}
