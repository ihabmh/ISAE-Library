package statistics;

import manager.LibraryManager;
import models.Media;
import models.Specialty;
import java.util.List;
import java.util.stream.Collectors;

public class MostAccessedBySpecialtyCommand implements StatisticsCommand {
    private Specialty specialty;
    private LibraryManager library;

    public MostAccessedBySpecialtyCommand(Specialty specialty, LibraryManager library) {
        this.specialty = specialty;
        this.library = library;
    }

    @Override
    public String execute() {
        List<Media> mediaList = library.getAllMedia().stream()
            .filter(m -> m.getSubjects().stream()
                .anyMatch(s -> s.getSpecialty().equals(specialty)))
            .sorted((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()))
            .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("Most Accessed Media for Specialty: ").append(specialty.getName()).append("\n");
        report.append("--------------------------------------------------\n");
        for (Media m : mediaList) {
            report.append(String.format("ID: %s | Title: %s | Type: %s | Access Count: %d\n",
                m.getId(), m.getTitle(), m.getType(), m.getAccessCount()));
        }
        return report.toString();
    }
}
