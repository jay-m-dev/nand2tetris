import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class Parser {
    private BufferedReader inputStream;
    private String currentCommand;
    //private Deque<String> commandsStack = new ArrayDeque<String>();
    private Queue<String> queue = new LinkedList<>();
    public Parser(String fileName) {
        // opens the file stream and gets ready to parse it
        String line = null;
        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            while ((line = inputStream.readLine()) != null) {
                // remove empty lines and comments
                line = line.trim();
                if (!(line.startsWith("//") || line.isEmpty())) {
                    // also remove inline comments
                    if (line.contains("//"))
                        line = line.split("//")[0].trim();
                    queue.add(line);
                }
            }
        } catch (IOException e) {
            System.err.format("Exception %s%n", e);
        }
    }

    public Boolean hasMoreCommands() { return (queue.peek() != null); }

    public void advance() { currentCommand = queue.poll(); }

    public Command commandType() {
        if (currentCommand.matches("add|sub|neg|eq|gt|lt|and|or|not"))
            return Command.C_ARITHMETIC;
        else if (currentCommand.startsWith("push"))
            return Command.C_PUSH;
        else if (currentCommand.startsWith("pop"))
            return Command.C_POP;
        else if (currentCommand.startsWith("label"))
            return Command.C_LABEL;
        else if (currentCommand.startsWith("goto"))
            return Command.C_GOTO;
        else if (currentCommand.startsWith("if"))
            return Command.C_IF;
        else if (currentCommand.startsWith("function"))
            return Command.C_FUNCTION;
        else if (currentCommand.startsWith("return"))
            return Command.C_RETURN;
        else if (currentCommand.startsWith("call"))
            return Command.C_CALL;
        return null;
    }

    public String arg1() {
        return currentCommand.split(" ")[1];
    }

    public int arg2() {
        return Integer.parseInt(currentCommand.split(" ")[2]);
    }

    public String getCurrentCommand() { return this.currentCommand; }
}

enum Command {
    C_ARITHMETIC,
    C_PUSH,
    C_POP,
    C_LABEL,
    C_GOTO,
    C_IF,
    C_FUNCTION,
    C_RETURN,
    C_CALL
}
