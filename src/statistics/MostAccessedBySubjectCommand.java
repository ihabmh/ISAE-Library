package statistics;

import models.Media;
import models.Subject;
import java.util.List;
import java.util.stream.Collectors;

public class MostAccessedBySubjectCommand implements StatisticsCommand {
    private Subject subject;

    public MostAccessedBySubjectCommand(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String execute() {
        List<Media> mediaList = subject.getMediaList().stream()
            .sorted((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()))
            .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("Most Accessed Media for Subject: ").append(subject.getCode()).append("\n");
        report.append("--------------------------------------------------\n");
        for (Media m : mediaList) {
            report.append(String.format("ID: %s | Title: %s | Type: %s | Access Count: %d\n",
                m.getId(), m.getTitle(), m.getType(), m.getAccessCount()));
        }
        return report.toString();
    }
}
