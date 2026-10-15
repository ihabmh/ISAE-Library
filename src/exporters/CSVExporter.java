package exporters;

import models.Media;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CSVExporter implements Exporter {
    @Override
    public void export(Object data, String filename) throws IOException {
        String csvContent = convertToCSV(data);
        Files.write(Paths.get(filename), csvContent.getBytes());
    }

    private String convertToCSV(Object data) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Title,Author,Year,Type,AccessCount\n");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (Object item : list) {
                if (item instanceof Media) {
                    Media m = (Media) item;
                    csv.append(m.getId()).append(",")
                       .append(escapeCSV(m.getTitle())).append(",")
                       .append(escapeCSV(m.getAuthor())).append(",")
                       .append(m.getPublicationYear()).append(",")
                       .append(m.getType()).append(",")
                       .append(m.getAccessCount()).append("\n");
                }
            }
        }
        return csv.toString();
    }

    private String escapeCSV(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
