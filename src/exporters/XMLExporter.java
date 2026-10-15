package exporters;

import models.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class XMLExporter implements Exporter {
    @Override
    public void export(Object data, String filename) throws IOException {
        String xmlContent = convertToXML(data);
        Files.write(Paths.get(filename), xmlContent.getBytes());
    }

    private String convertToXML(Object data) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<library>\n");
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            for (Object item : list) {
                if (item instanceof Media) {
                    Media m = (Media) item;
                    xml.append("  <media type=\"").append(m.getType()).append("\">\n");
                    xml.append("    <id>").append(m.getId()).append("</id>\n");
                    xml.append("    <title>").append(m.getTitle()).append("</title>\n");
                    xml.append("    <author>").append(m.getAuthor()).append("</author>\n");
                    xml.append("    <year>").append(m.getPublicationYear()).append("</year>\n");
                    xml.append("    <description>").append(m.getDescription()).append("</description>\n");
                    xml.append("    <accessCount>").append(m.getAccessCount()).append("</accessCount>\n");
                    if (m instanceof Document) {
                        xml.append("    <pages>").append(((Document) m).getNumberOfPages()).append("</pages>\n");
                    } else if (m instanceof VideoSession) {
                        xml.append("    <duration>").append(((VideoSession) m).getDuration()).append("</duration>\n");
                    } else if (m instanceof OnlineQuiz) {
                        xml.append("    <estimatedDuration>").append(((OnlineQuiz) m).getEstimatedDuration()).append("</estimatedDuration>\n");
                        xml.append("    <difficulty>").append(((OnlineQuiz) m).getDifficultyLevel()).append("</difficulty>\n");
                    }
                    xml.append("  </media>\n");
                }
            }
        }
        xml.append("</library>");
        return xml.toString();
    }
}
