package hk.ust.comp4321.utils;
import java.io.IOException;
import java.util.Vector;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class writeVectorToFile {
    public static void writeVectorToFile(Vector<String> vector, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String element : vector) {
                writer.write(element);
                writer.newLine();
            }
            System.out.println("Vector elements successfully written to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing the file: " + e.getMessage());
        }
    }
}
