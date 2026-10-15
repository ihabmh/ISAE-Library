package exporters;

import models.Media;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JSONExporter implements Exporter {
    @Override
    public void export(Object data, String filename) throws IOException {
        String jsonContent = convertToJSON(data);
        Files.write(Paths.get(filename), jsonContent.getBytes());
    }

    private String convertToJSON(Object data) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (item instanceof Media) {
                    Media m = (Media) item;
                    json.append("  {\n");
                    json.append("    \"id\": \"").append(m.getId()).append("\",\n");
                    json.append("    \"title\": \"").append(m.getTitle()).append("\",\n");
                    json.append("    \"author\": \"").append(m.getAuthor()).append("\",\n");
                    json.append("    \"year\": ").append(m.getPublicationYear()).append(",\n");
                    json.append("    \"type\": \"").append(m.getType()).append("\",\n");
                    json.append("    \"accessCount\": ").append(m.getAccessCount()).append("\n");
                    json.append("  }");
                    if (i < list.size() - 1) {
                        json.append(",");
                    }
                    json.append("\n");
                }
            }
        }
        json.append("]");
        return json.toString();
    }
}
