import java.io.PrintWriter;
import java.util.HashMap;

public class VMWriter {
    PrintWriter out;
    HashMap<Segment, String> segments;
    HashMap<Command, String> commands;
    public VMWriter(PrintWriter p) {
        this.out = p;

        segments = new HashMap<>();
        segments.put(Segment.CONST, "constant");
        segments.put(Segment.ARG, "argument");
        segments.put(Segment.LOCAL, "local");
        segments.put(Segment.STATIC, "static");
        segments.put(Segment.THIS, "this");
        segments.put(Segment.THAT, "that");
        segments.put(Segment.POINTER, "pointer");
        segments.put(Segment.TEMP, "temp");

        commands = new HashMap<>();
        commands.put(Command.ADD, "add");
        commands.put(Command.SUB, "sub");
        commands.put(Command.NEG, "neg");
        commands.put(Command.EQ, "eq");
        commands.put(Command.GT, "gt");
        commands.put(Command.LT, "lt");
        commands.put(Command.AND, "and");
        commands.put(Command.OR, "or");
        commands.put(Command.NOT, "not");
        commands.put(Command.MULT, "call Math.multiply 2");
        commands.put(Command.DIV, "call Math.divide 2");
    }

    public void writePush(Segment s, int index) {
        out.println("push " + segments.get(s) + " " + index);
    }

    public void writePop(Segment s, int index) {
        out.println("pop " + segments.get(s) + " " + index);
    }

    public void writeArithmetic(Command c) {
        out.println(commands.get(c));
    }

    public void writeLabel(String label) {
        out.println("label " + label);
    }

    public void writeGoto(String label) {
        out.println("goto " + label);
    }

    public void writeIf(String label) {
        out.println("if-goto " + label);
    }

    public void writeCall(String name, int nArgs) {
        out.println("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nLocals) {
        out.println("function " + name + " " + nLocals);
    }

    public void writeReturn() {
        out.println("return");
    }

    public void close() {
        if (out != null) {
            out.close();
        }
    }
}
