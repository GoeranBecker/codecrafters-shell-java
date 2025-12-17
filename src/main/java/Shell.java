import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.function.Function;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;

import java.io.IOException;
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Shell {
    private boolean running = true;
    private final Map<String, Function<ExecutionContext, String>> builtins = new HashMap<>();

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
                this.args = new String[0];
            }
        }
    }

    public Shell() {
        this.builtins.put("exit", this::exit);
        this.builtins.put("echo", this::echo);
        this.builtins.put("type", this::type);
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
            method = resolveBuiltins(ec.commandName);
            // if (method == null) {
            //     final String path = this.resolvePATH(ec.commandName);
            //     if (path != null) method = _ -> path;
            // }
            if (method == null) {
                this.println(ec.commandName+ ": command not found");
                continue;
            }
            result = method.apply(ec);
            if (result != null) this.println(result);
        }
    }

    private Function<ExecutionContext, String> resolveBuiltins(final String command) {
        if (command == null || command.isEmpty()) return null;
        return this.builtins.get(command);
    }
    
    private String resolvePATH(final String command) {
        if (command == null || command.isEmpty()) return null;
        
        final String PATH = System.getenv("PATH");
        Optional<String> exePath;
        for(String dir: PATH.split(File.pathSeparator)){
            try (Stream<Path> stream = Files.list(Paths.get(dir))) {
                exePath = stream
                    .filter(path -> Files.isRegularFile(path))
                    .filter(path-> Files.isExecutable(path))
                    .map(path -> path.toString())
                    .filter(file -> file.endsWith(File.separator + command))
                    .findFirst();
            } catch (IOException e) {
                continue;
            }
            if (exePath.isPresent()) return exePath.get();
        }
        return null;
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
        if (ec.args.length == 0) return "";
        return String.join(" ", ec.args);
    }

    private String type (final ExecutionContext ec) {
        if (ec.args.length == 0) return "no command specified";

        if (this.resolveBuiltins(ec.args[0]) != null) {
            return ec.args[0] + " is a shell builtin";
        }
        
        final String path = resolvePATH(ec.args[0]);
        if (path != null && !path.isEmpty()) {
            return ec.args[0] + " is " + path;
        }
        return ec.args[0] + ": not found";
    }
}
