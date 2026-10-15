package statistics;

import manager.LibraryManager;
import models.Media;

// Command to calculate the total number of accesses across all media
public class TotalAccessReportCommand implements StatisticsCommand {
    private LibraryManager library;

    public TotalAccessReportCommand(LibraryManager library) {
        this.library = library;
    }

    @Override
    public String execute() {
        int total = library.getAllMedia().stream().mapToInt(Media::getAccessCount).sum();
        return "Total media accesses: " + total;
    }
}
