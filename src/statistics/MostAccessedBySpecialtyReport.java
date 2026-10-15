package statistics;

import manager.LibraryManager;
import models.Media;
import models.Specialty;
import java.util.List;
import java.util.stream.Collectors;

public class MostAccessedBySpecialtyReport extends StatisticReport {
    private Specialty specialty;
    private LibraryManager library;

    public MostAccessedBySpecialtyReport(Specialty specialty, LibraryManager library) {
        this.specialty = specialty;
        this.library = library;
    }

    @Override
    protected List<Media> collectData() {
        return library.getAllMedia().stream()
            .filter(m -> m.getSubjects().stream()
                .anyMatch(s -> s.getSpecialty().equals(specialty)))
            .collect(Collectors.toList());
    }

    @Override
    protected String formatReport(List<Media> data) {
        StringBuilder report = new StringBuilder();
        report.append("Most Accessed Media for Specialty: ").append(specialty.getName()).append("\n");
        report.append("--------------------------------------------------\n");
        for (Media m : data) {
            report.append(String.format("ID: %s | Title: %s | Type: %s | Access Count: %d\n",
                m.getId(), m.getTitle(), m.getType(), m.getAccessCount()));
        }
        return report.toString();
    }
}
