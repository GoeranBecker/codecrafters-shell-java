import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.function.Function;

public class Shell {
    private boolean running = true;
    private final Map<String, Function<ExecutionContext, String>> commands = new HashMap<>();

    // TODO: refactor
    private static class ExecutionContext {
        public final String commandName;
        public final String[] args;

        public ExecutionContext(final String command) {
            final String[] allArgs = command.split(" ");
            this.commandName = allArgs[0];
            if (allArgs.length > 1) {
                this.args = Arrays.copyOfRange(allArgs, 1, allArgs.length);
            } else {
                this.args = null;
            }
        }
    }

    public Shell() {
        this.commands.put("exit", this::exit);
        this.commands.put("echo", this::echo);
    }

    public void start() {
        var scanner = new Scanner(System.in);
        String command;
        ExecutionContext ec;
        Function<ExecutionContext, String> method;
        while (this.running) {
            System.out.print("$ ");
            command = scanner.nextLine();
            ec = new ExecutionContext(command);
            method = resolveCommand(ec.commandName);
            if (method == null) {
                System.out.println(ec.commandName+ ": command not found");
                continue;
            }
            System.out.println(method.apply(ec));
        }
    }

    private Function<ExecutionContext, String> resolveCommand(final String command) {
        if (command == null || command.isEmpty()) return null;
        return this.commands.get(command);
    }

  //TODO: refactor commands
    private String exit(final ExecutionContext ec) {
        this.running = false;
        return "Bye";
    }

    private String echo(final ExecutionContext ec) {
        if (ec.args == null) return "";
        return String.join(" ", ec.args);
    }
}
