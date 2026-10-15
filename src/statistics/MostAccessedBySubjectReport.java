package statistics;

import models.Media;
import models.Subject;
import java.util.ArrayList;
import java.util.List;

public class MostAccessedBySubjectReport extends StatisticReport {
    private Subject subject;

    public MostAccessedBySubjectReport(Subject subject) {
        this.subject = subject;
    }

    @Override
    protected List<Media> collectData() {
        return new ArrayList<>(subject.getMediaList());
    }

    @Override
    protected String formatReport(List<Media> data) {
        StringBuilder report = new StringBuilder();
        report.append("Most Accessed Media for Subject: ").append(subject.getCode()).append("\n");
        report.append("--------------------------------------------------\n");
        for (Media m : data) {
            report.append(String.format("ID: %s | Title: %s | Type: %s | Access Count: %d\n",
                m.getId(), m.getTitle(), m.getType(), m.getAccessCount()));
        }
        return report.toString();
    }
}
