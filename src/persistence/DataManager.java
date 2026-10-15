package persistence;

import manager.LibraryManager;
import models.LibraryData;
import models.Media;

import java.io.*;
import java.util.List;

// Handles saving and loading the library's state using binary serialization
public class DataManager {
    private static final String DATA_FILE = "library_data.ser";

    // Serializes the library data to a binary file
    public static void saveData(LibraryManager manager) {
        LibraryData data = new LibraryData(
            manager.getAllMedia(),
            manager.getStudents(),
            manager.getSpecialties()
        );
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(data);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Deserializes the library data from a binary file
    public static void loadData(LibraryManager manager) {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No saved data found. Starting fresh.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            LibraryData data = null;

            if (obj instanceof LibraryData) {
                data = (LibraryData) obj;
            } else if (obj instanceof List) {
                // Support legacy format
                data = new LibraryData((List<Media>) obj, null, null);
            }
            
            if (data != null) {
                if (data.getAllMedia() != null) manager.setAllMedia(data.getAllMedia());
                if (data.getStudents() != null) manager.setStudents(data.getStudents());
                if (data.getSpecialties() != null) manager.setSpecialties(data.getSpecialties());
            }
            System.out.println("Data loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
}
