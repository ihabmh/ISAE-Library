package persistence;

import factory.ConcreteMediaFactory;
import factory.DocumentCreator;
import factory.QuizCreator;
import factory.VideoCreator;
import models.Media;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Utility class to import media items from external files (XML and CSV)
public class MediaImporter {
    private static ConcreteMediaFactory factory;

    static {
        // Initialize the media factory with supported creators
        factory = new ConcreteMediaFactory();
        factory.registerCreator("Document", new DocumentCreator());
        factory.registerCreator("VideoSession", new VideoCreator());
        factory.registerCreator("OnlineQuiz", new QuizCreator());
    }

    // Imports media from an XML file
    public static List<Media> importFromXML(File file) throws Exception {
        List<Media> importedMedia = new ArrayList<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        NodeList mediaNodes = doc.getElementsByTagName("media");
        for (int i = 0; i < mediaNodes.getLength(); i++) {
            Element element = (Element) mediaNodes.item(i);
            String type = element.getAttribute("type");
            Map<String, Object> attrs = new HashMap<>();

            attrs.put("id", getTagValue("id", element));
            attrs.put("title", getTagValue("title", element));
            attrs.put("author", getTagValue("author", element));
            attrs.put("publicationYear", Integer.parseInt(getTagValue("year", element)));
            attrs.put("description", getTagValue("description", element));

            if ("Document".equals(type)) {
                attrs.put("numberOfPages", Integer.parseInt(getTagValue("pages", element)));
            } else if ("VideoSession".equals(type)) {
                attrs.put("duration", Integer.parseInt(getTagValue("duration", element)));
            } else if ("OnlineQuiz".equals(type)) {
                attrs.put("estimatedDuration", Integer.parseInt(getTagValue("estimatedDuration", element)));
                attrs.put("difficultyLevel", getTagValue("difficulty", element));
            }

            importedMedia.add(factory.createMedia(type, attrs));
        }
        return importedMedia;
    }

    // Imports media from a CSV file
    public static List<Media> importFromCSV(File file) throws Exception {
        List<Media> importedMedia = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip Header
            while ((line = br.readLine()) != null) {
                String[] values = parseCSVLine(line);
                if (values.length < 6) continue;

                Map<String, Object> attrs = new HashMap<>();
                attrs.put("id", values[0]);
                attrs.put("title", values[1]);
                attrs.put("author", values[2]);
                attrs.put("publicationYear", Integer.parseInt(values[3]));
                attrs.put("description", "");
                
                String type = values[4];

                if ("Document".equals(type)) {
                    attrs.put("numberOfPages", 0);
                } else if ("VideoSession".equals(type)) {
                    attrs.put("duration", 0);
                } else if ("OnlineQuiz".equals(type)) {
                    attrs.put("estimatedDuration", 0);
                    attrs.put("difficultyLevel", "Unknown");
                }

                importedMedia.add(factory.createMedia(type, attrs));
            }
        }
        return importedMedia;
    }

    // Helper to get text content from an XML tag
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        if (nodeList.getLength() == 0) return "";
        return nodeList.item(0).getNodeValue();
    }

    // Basic CSV line parser handling quotes
    private static String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());
        return values.toArray(new String[0]);
    }
}
