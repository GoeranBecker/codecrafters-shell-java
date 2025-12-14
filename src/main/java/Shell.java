import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Shell {
    private boolean running = true;
    private final Map<String, Runnable> commands = new HashMap<>();

    public Shell() {
        this.commands.put("exit", this::exit);
    }

    public void start() {
        var scanner = new Scanner(System.in);
        String command;
        Runnable method;
        while (this.running) {
            System.out.print("$ ");
            command = scanner.nextLine();
            method = resolveCommand(command);
            if (method == null) {
                System.out.println(command + ": command not found");
                continue;
            }
            method.run();
        }
    }

    private Runnable resolveCommand(final String command) {
        if (command == null || command.isEmpty()) return null;
        return this.commands.get(command);
    }

    private void exit() {
        this.running = false;
    }
}
