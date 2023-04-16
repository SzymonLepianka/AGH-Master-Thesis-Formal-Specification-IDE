package sl.fside.services.code_generator1.io;

import java.util.Scanner;

public class DataReader {
    private Scanner sc = new Scanner(System.in);

    public int getInt() {
        int number = sc.nextInt();
        sc.nextLine();
        return number;
    }

    public void close() {
        sc.close();
    }
}
