package org.example.io;

import java.util.Scanner;

public class DataReader {
    private final Scanner sc = new Scanner(System.in);

    public int getInt() {
        int number = sc.nextInt();
        sc.nextLine();
        return number;
    }

    public String getString() {
        return sc.nextLine();
    }

    public void close() {
        sc.close();
    }
}
