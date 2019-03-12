import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;

public class CodeWriter {
    private String inputFile;
    private PrintWriter out;
    int stackBase = 256;
    private Deque<String> argument = new ArrayDeque<String>();
    public CodeWriter() {
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("MyProg.asm")));
            out.println("@256" + "\n" + "D=A" + "\n" + "@SP" + "\n" + "M=D" + "\n"); // set SP
        } catch (IOException e) {
            System.err.format("Exception: %s%n", e);
        }
    }
    public void setFileName(String fileName) {
        this.inputFile = fileName;
    }

    public void writeArithmetic(String command) {
        // the command in
        String line = "";
        if (command.equalsIgnoreCase("add")) {
            // 2 pops 1 push
            WritePushPop(Command.C_POP, "add", 0 );
            // line += "D=M" + "\n"; // M is the value at the POPPED register
            out.println("D=M" + "\n"); // M is the value at the POPPED register
            WritePushPop(Command.C_POP, "add", 0 );
            // line += "D=D+M" + "\n"; // D is the D selected on the previous M, M is the value at the POPPED register
            out.println("D=D+M" + "\n"); // D is the D selected on the previous M, M is the value at the POPPED register
            WritePushPop(Command.C_PUSH, "add", 0);
            // SP(false);
            // line += "@" + stackBase + "\n";
            // line += "D=M" + "\n";
            // SP(false);
            // line += "@" + stackBase + "\n";
            // line += "D=D+M" + "\n";
            // out.println(line);

        }

    }

    public void WritePushPop(Command command, String segment, int index) {
        String line = "";
        if (command == Command.C_PUSH) {
            // line = selectSP(true);
            // line = "@SP" + "\n";
            // line += "AM=M+1" + "\n"; // select the address at SP, and increment the value at SP
            // line += "M=M+1"; // increase SP
            // if constant, select the A register with index
            if (segment.equalsIgnoreCase("constant")) {
                line += "@" + index + "\n";
                line += "D=A" + "\n";
            }
            line += "@SP" + "\n";
            line += "A=M" + "\n";
            line += "M=D" + "\n";
            line += "@SP" + "\n";
            line += "M=M+1" + "\n"; // select the address at SP, and increment the value at SP
        } else if (command == Command.C_POP) {
            // SP(false);
            line = "@SP" + "\n";
            line += "M=M-1" + "\n";
            line += "A=M" + "\n"; // select the previous item in the stack
            // line = "@" + stackBase + "\n";
            // line += "M=" + index + "\n"; // put the value in the D register
        }
        // System.out.println("gonna write it? " + line);
        // System.out.println(out.toString());
        out.println(line);
    }

    public String selectSP(Boolean increase) {
        String line = "";
        line += "@SP" + "\n";
        line += "A=M" + "\n";
        line += "M=M" + (increase ? "+" : "-") + "1" + "\n";
        return line;
    }

    public void Close() {
        if (out != null)
            out.println("(END)");
            out.println("@END");
            out.println("0;JMP");
            out.close();
    }
}