package org.example;

import org.example.io.DataReader;

import java.io.IOException;

import static org.example.functions.GenJava.genJava;
import static org.example.functions.GenPython.genPython;

/**
 * The Code Parser program implements an application that
 * generate Java and Python code from patterns
 *
 * @author Kacper Dworak
 * @author Daniel Sikora
 * @author Krzysztof Ksiazek
 * @version 4.20
 * @since 2023-02-27
 */
public class Main {
    public static void main(String[] arg) throws IOException {
        final int EXIT = 0;
        final int JAVA = 1;
        final int PYTHON = 2;
        DataReader dataReader = new DataReader();
        String results;
        int option;
        do {
            printOptions();
            option = dataReader.getInt();
            String UUID;
            System.out.println("\n");
            System.out.print("Enter UUID: ");
            switch (option) {
                case JAVA -> {
                    UUID = dataReader.getString();
                    System.out.print("\n");
                    results = genJava(null, UUID);

                }
                case PYTHON -> {
                    UUID = dataReader.getString();
                    System.out.print("\n");
                    results = genPython(null, UUID);

                }
                case EXIT -> {
                    dataReader.close();
                    System.out.println("Bye, Bye!");
                }
                default -> System.out.println("Not valid language!");
            }
            System.out.println("\n\n\n\n");
        } while (option != EXIT);
    }

    private static void printOptions() {
        System.out.println("Select options: ");
        System.out.println("1 -> Java");
        System.out.println("2 -> Python");
        System.out.println("0 -> Exit");
    }


}
