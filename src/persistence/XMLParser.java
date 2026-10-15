package persistence;

import manager.LibraryManager;
import models.Specialty;
import models.Student;
import models.Subject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Handles XML parsing and generation for student data
public class XMLParser {
    
    // Loads student information from an XML file
    public static void loadStudentData(String filename, LibraryManager manager) {
        try {
            File inputFile = new File(filename);
            if (!inputFile.exists()) {
                System.out.println("Student XML file not found: " + filename);
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList specialtyNodes = doc.getElementsByTagName("specialite");
            for (int i = 0; i < specialtyNodes.getLength(); i++) {
                Element specialtyElement = (Element) specialtyNodes.item(i);
                String specialtyName = specialtyElement.getAttribute("nom");
                
                Specialty specialty = manager.getSpecialties().stream()
                        .filter(s -> s.getName().equals(specialtyName))
                        .findFirst().orElse(null);
                
                if (specialty == null) {
                    specialty = new Specialty(specialtyName);
                    manager.addSpecialty(specialty);
                }

                NodeList studentNodes = specialtyElement.getElementsByTagName("etudiant");
                for (int j = 0; j < studentNodes.getLength(); j++) {
                    Element studentElement = (Element) studentNodes.item(j);
                    String username = studentElement.getAttribute("username");
                    String password = studentElement.getAttribute("password");

                    String finalUsername = username;
                    Student existingStudent = manager.getStudents().stream()
                            .filter(s -> s.getUsername().equals(finalUsername))
                            .findFirst().orElse(null);

                    if (existingStudent == null) {
                        Student student = new Student(username, password, specialty);

                        NodeList valeurNodes = studentElement.getElementsByTagName("valeur");
                        for (int k = 0; k < valeurNodes.getLength(); k++) {
                            String subjectCode = valeurNodes.item(k).getTextContent().trim();

                            String finalSubjectCode = subjectCode;
                            Subject subject = specialty.getSubjects().stream()
                                    .filter(s -> s.getCode().equals(finalSubjectCode))
                                    .findFirst().orElse(null);

                            if (subject == null) {
                                subject = new Subject(subjectCode, specialty);
                                specialty.addSubject(subject);
                            }

                            student.addSubject(subject);
                        }
                        manager.addStudent(student);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing student XML: " + e.getMessage());
        }
    }

    // Saves student information to an XML file
    public static void saveStudentData(String filename, LibraryManager manager) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("issae");
            doc.appendChild(rootElement);

            for (models.Specialty specialty : manager.getSpecialties()) {
                Element specialtyElement = doc.createElement("specialite");
                specialtyElement.setAttribute("nom", specialty.getName());
                rootElement.appendChild(specialtyElement);

                for (models.Student student : manager.getStudents()) {
                    if (student.getUsername().equalsIgnoreCase("admin")) continue;
                    
                    if (student.getSpecialty().equals(specialty)) {
                        Element studentElement = doc.createElement("etudiant");
                        studentElement.setAttribute("username", student.getUsername());
                        studentElement.setAttribute("password", student.getPassword());
                        specialtyElement.appendChild(studentElement);

                        for (models.Subject subject : student.getSubjects()) {
                            Element valeurElement = doc.createElement("valeur");
                            valeurElement.appendChild(doc.createTextNode(subject.getCode()));
                            studentElement.appendChild(valeurElement);
                        }
                    }
                }
            }

            // Write content to XML file
            javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(new File(filename));
            transformer.transform(source, result);

            System.out.println("Student data saved to " + filename);
        } catch (Exception e) {
            System.err.println("Error saving student XML: " + e.getMessage());
        }
    }
}
