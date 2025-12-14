import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
       // 1
        System.out.print("$ ");
        // 2
        var scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        System.out.println(command + ": command not found");
    }
}
