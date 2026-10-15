package statistics;

import models.Media;
import java.util.List;

public abstract class StatisticReport {
    
    // Template Method
    public final String generateReport() {
        List<Media> data = collectData();
        List<Media> processedData = processData(data);
        return formatReport(processedData);
    }

    protected abstract List<Media> collectData();

    protected List<Media> processData(List<Media> data) {
        // Default processing: sort by access count descending
        data.sort((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()));
        return data;
    }

    protected abstract String formatReport(List<Media> data);
}
