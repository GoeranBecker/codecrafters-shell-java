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
        this.commands.put("type", this::type);
    }

    public void start() {
        var scanner = new Scanner(System.in);
        String command;
        ExecutionContext ec;
        Function<ExecutionContext, String> method;
        String result;
        while (this.running) {
            this.print("$ ");
            command = scanner.nextLine();
            ec = new ExecutionContext(command);
            method = resolveCommand(ec.commandName);
            if (method == null) {
                this.println(ec.commandName+ ": command not found");
                continue;
            }
            result = method.apply(ec);
            if (result != null) this.println(result);
        }
    }

    private Function<ExecutionContext, String> resolveCommand(final String command) {
        if (command == null || command.isEmpty()) return null;
        return this.commands.get(command);
    }

    // wrap for clarity
    private void println(final String out) {
        System.out.println(out);
    }

    // wrap for clarity
    private void print(final String out) {
        System.out.print(out);
    }

  //TODO: refactor commands
    private String exit(final ExecutionContext ec) {
        this.running = false;
        return null;
    }

    private String echo(final ExecutionContext ec) {
        if (ec.args == null) return "";
        return String.join(" ", ec.args);
    }

    private String type (final ExecutionContext ec) {
        if (ec.args != null && this.commands.containsKey(ec.args[0])) {
            return ec.args[0] + " is a shell builtin";
        }
        // TODO: command source, builtin or otherwise
        if (ec.args != null) {
            return ec.args[0] + ": not found";
        }
        return "no command specified";
    }
}
