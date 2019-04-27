import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;

public class CodeWriter {
    private String inputFile;
    private PrintWriter out;
    // int stackBase = 256;
    private Deque<String> argument = new ArrayDeque<String>();
    private static int counter = 0;
    private String funcName = "";


    public CodeWriter(String fileName) {
        try {

//            setFileName(fileName);
            String outFile = fileName;
            if (fileName.contains(".")) {
                outFile = fileName.substring(0, fileName.indexOf("."));
            }
            outFile += ".asm";
            out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            // no need to initialize
            // will be taken care of by Sys.init
            // out.println("@256" + "\n" + "D=A" + "\n" + "@SP" + "\n" + "M=D" + "\n"); // set SP
            // writeInit();
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
            // // 2 pops 1 push
            // WritePushPop(Command.C_POP, "add", 0 );
            // out.println("D=M" + "\n"); // M is the value at the POPPED register
            // WritePushPop(Command.C_POP, "add", 0 );
            // out.println("D=D+M" + "\n"); // D is the D selected on the previous M, M is the value at the POPPED register
            // WritePushPop(Command.C_PUSH, "add", 0);
            // add();
            line += "@SP"    + "\n";
            line += "AM=M-1" + "\n";
            line += "D=M"    + "\n";
            line += "A=A-1"  + "\n";
            line += "M=D+M"  + "\n";
        } else if (command.equalsIgnoreCase("sub")) {
            // // 2 pops 1 push
            // WritePushPop(Command.C_POP, "sub", 0 );
            // out.println("D=M" + "\n"); // M is the value at the POPPED register
            // WritePushPop(Command.C_POP, "sub", 0 );
            // out.println("D=D-M" + "\n"); // D is the D selected on the previous M, M is the value at the POPPED register
            // WritePushPop(Command.C_PUSH, "sub", 0);
            // sub();
            line += "@SP"    + "\n";
            line += "AM=M-1" + "\n";
            line += "D=M"    + "\n";
            line += "A=A-1"  + "\n";
            line += "M=M-D"  + "\n";
            
        } else if (command.equalsIgnoreCase("eq")) {
            // comparing two values equates to subtracting them
            // and checking if they are 0
            // pop the two values
            // sub();
            String sCounter = Integer.toString(++counter);
            line += "@SP" + "\n";
            line += "AM=M-1" + "\n";
            line += "D=M"    + "\n";
            line += "A=A-1"  + "\n";
            line += "D=M-D"  + "\n";
            line += "@EQ.TRUE." + sCounter + "\n";
            line += "D;JEQ"  + "\n";
            line += "@SP"    + "\n";
            line += "A=M-1"  + "\n";
            line += "M=0"    + "\n";
            line += "@EQ.AFTER." + sCounter + "\n";
            line += "0;JMP"  + "\n";
            line += "(EQ.TRUE." + sCounter + ")" + "\n";
            line += "@SP"    + "\n";
            line += "A=M-1"  + "\n";
            line += "M=-1"   + "\n";
            line += "(EQ.AFTER." + sCounter + ")" + "\n";
        } else if (command.equalsIgnoreCase("lt")) {
            String sCounter = Integer.toString(++counter);
            line += "@SP"    + "\n";
            line += "AM=M-1" + "\n";
            line += "D=M"    + "\n";
            line += "A=A-1"  + "\n";
            line += "D=M-D"  + "\n";
            line += "@LT.TRUE." + sCounter + "\n";
            line += "D;JLT"  + "\n";
            line += "@SP" + "\n";
            line += "A=M-1"  + "\n";
            line += "M=0"    + "\n";
            line += "@LT.AFTER." + sCounter + "\n";
            line += "0;JMP"  + "\n";
            line += "(LT.TRUE. " + sCounter + ")" + "\n";
            line += "@SP"    + "\n";
            line += "A=M-1"  + "\n";
            line += "M=-1"   + "\n";
            line += "(LT.AFTER." + sCounter + ")" + "\n";
        } else if (command.equalsIgnoreCase("gt")) {
            String sCounter = Integer.toString(++counter);
            line += "@SP"        + "\n";
            line += "AM=M-1"     + "\n";
            line += "D=M"        + "\n";
            line += "A=A-1"      + "\n";
            line += "D=M-D"      + "\n";
            line += "@GT.TRUE."  + sCounter + "\n";
            line += "D;JGT"      + "\n";
            line += "@SP"        + "\n";
            line += "A=M-1"      + "\n";
            line += "M=0"        + "\n";
            line += "@GT.AFTER." + sCounter + "\n";
            line += "0;JMP"      + "\n";
            line += "(GT.TRUE."  + sCounter + ")" + "\n";
            line += "@SP"        + "\n";
            line += "A=M-1"      + "\n";
            line += "M=-1"       + "\n";
            line += "(GT.AFTER." + sCounter + ")" + "\n";
        } else if (command.equalsIgnoreCase("neg")) {
            line += "@SP"   + "\n";
            line += "A=M-1" + "\n";
            line += "M=-M"  + "\n";
        } else if (command.equalsIgnoreCase("and")) {
            line += "@SP"    + "\n";
            line += "AM=M-1" + "\n";
            line += "D=M"    + "\n";
            line += "A=A-1"  + "\n";
            line += "M=D&M"  + "\n";
        } else if (command.equalsIgnoreCase("or")) {
            line += "@SP"    + "\n";
            line += "AM=M-1" + "\n";
            line += "D=M"    + "\n";
            line += "A=A-1"  + "\n";
            line += "M=D|M"  + "\n";
        } else if (command.equalsIgnoreCase("not")) {
            line += "@SP"    + "\n";
            line += "A=M-1"  + "\n";
            line += "M=!M"   + "\n";
        }

        out.println(line);
    }

    public void writeInit() {
        String line = "";
        line += "@256" + "\n";
        line += "D=A"  + "\n";
        line += "@SP"  + "\n";
        line += "M=D"  + "\n";

        out.println(line);
        
        writeCall("Sys.init", 0);

        line = "0;JMP" + "\n";

        out.println(line);

    }

    public void writeReturn() {
        String line = "";
        // *(LCL - 5) -> R13
        line += "@LCL"   + "\n";
        line += "D=M"    + "\n";
        line += "@5"     + "\n";
        line += "A=D-A"  + "\n";
        line += "D=M"    + "\n";
        line += "@R13"   + "\n";
        line += "M=D"    + "\n";
        // *(SP - 1) -> *ARG
        line += "@SP"    + "\n";
        line += "A=M-1"  + "\n";
        line += "D=M"    + "\n";
        line += "@ARG"   + "\n";
        line += "A=M"    + "\n";
        line += "M=D "   + "\n";
        // ARG + 1 -> SP
        line += "D=A+1"  + "\n";
        line += "@SP"    + "\n";
        line += "M=D"    + "\n";
        // *(LCL - 1) -> THAT; LCL--
        line += "@LCL"   + "\n";
        line += "AM=M-1" + "\n";
        line += "D=M"    + "\n";
        line += "@THAT"  + "\n";
        line += "M=D"    + "\n";
        // *(LCL - 1) -> THIS; LCL--
        line += "@LCL"   + "\n";
        line += "AM=M-1" + "\n";
        line += "D=M"    + "\n";
        line += "@THIS"  + "\n";
        line += "M=D"    + "\n";
        // *(LCL - 1) -> ARG; LCL--
        line += "@LCL"   + "\n";
        line += "AM=M-1" + "\n";
        line += "D=M"    + "\n";
        line += "@ARG"   + "\n";
        line += "M=D"    + "\n";
        // *(LCL - 1) -> LCL
        line += "@LCL"   + "\n";
        line += "A=M-1"  + "\n";
        line += "D=M"    + "\n";
        line += "@LCL"   + "\n";
        line += "M=D"    + "\n";
        // R13 -> A
        line += "@R13"   + "\n";
        line += "A=M"    + "\n";
        line += "0;JMP"  + "\n";

        out.println(line);

    }

    public void writeLabel(String label) {
        String line = "(" + this.funcName + "$" + label +  ")" + "\n";

        out.println(line);
    }

    public void writeFunction(String functionName, int numLocals) {
        this.funcName = functionName; // store the functin name
        String line = "";
        line += "(" + functionName + ")" + "\n";
        line += "@SP"                    + "\n";
        line += "A=M"                    + "\n";
        for (int i = 0; i < numLocals; i++) {
            line += "M=0"   + "\n";
            line += "A=A+1" + "\n";
        }
        line += "D=A" + "\n";
        line += "@SP" + "\n";
        line += "M=D" + "\n";

        out.println(line);
    }

    public void writeGoto(String label) {
        String line = "";
        line += "@" + this.funcName + "$" + label + "\n";
        line += "0;JMP"                           + "\n";

        out.println(line);
    }

    public void writeIf(String label) {
        String line = "";
        line += "@SP"    + "\n";
        line += "AM=M-1" + "\n";
        line += "D=M"    + "\n";
        line += "@" + this.funcName + "$" + label + "\n";
        line += "D;JNE"  + "\n";

        out.println(line);
    }

    public void writeCall(String functionName, int numArgs) {
        String line = "";
        this.counter++;
        // SP -> R13
        line += "@SP"      + "\n";
        line += "D=M"      + "\n";
        line += "@R13"     + "\n";
        line += "M=D"      + "\n";
        // @RET -> *SP
        line += "@RET." + this.counter + "\n";
        line += "D=A"      + "\n";
        line += "@SP"      + "\n";
        line += "A=M"      + "\n";
        line += "M=D"      + "\n";
        // SP++
        line += "@SP"      + "\n";
        line += "M=M+1"    + "\n";
        // LCL -> *SP
        line += "@LCL"     + "\n";
        line += "D=M"      + "\n";
        line += "@SP"      + "\n";
        line += "A=M"      + "\n";
        line += "M=D"      + "\n";
        // SP++
        line += "@SP"      + "\n";
        line += "M=M+1"    + "\n";
        // ARG -> *SP
        line += "@ARG"     + "\n";
        line += "D=M"      + "\n";
        line += "@SP"      + "\n";
        line += "A=M"      + "\n";
        line += "M=D"      + "\n";
        // SP++
        line += "@SP"      + "\n";
        line += "M=M+1"    + "\n";
        // THIS -> *SP
        line += "@THIS"    + "\n";
        line += "D=M"      + "\n";
        line += "@SP"      + "\n";
        line += "A=M"      + "\n";
        line += "M=D"      + "\n";
        // SP++
        line += "@SP"      + "\n";
        line += "M=M+1"    + "\n";
        // THAT -> *SP
        line += "@THAT"    + "\n";
        line += "D=M"      + "\n";
        line += "@SP"      + "\n";
        line += "A=M"      + "\n";
        line += "M=D"      + "\n";
        // SP++
        line += "@SP"      + "\n";
        line += "M=M+1"    + "\n";
        // R13 - n -> ARG
        line += "@R13"     + "\n";
        line += "D=M"      + "\n";
        line += "@"        + numArgs + "" + "\n";
        line += "D=D-A"    + "\n";
        line += "@ARG"     + "\n";
        line += "M=D"      + "\n";
        // SP -> LCL
        line += "@SP"      + "\n";
        line += "D=M"      + "\n";
        line += "@LCL"     + "\n";
        line += "M=D"      + "\n";
        line += "@" + functionName + "" + "\n";
        line += "0;JMP"    + "\n";
        line += "(RET." + this.counter + ")" + "\n";

        out.println(line);
        
    }

    // public void add() {
    //     // 2 pops 1 push
    //     WritePushPop(Command.C_POP, "add", 0 );
    //     out.println("D=M" + "\n"); // M is the value at the POPPED register
    //     WritePushPop(Command.C_POP, "add", 0 );
    //     out.println("D=D+M" + "\n"); // D is the D selected on the previous M, M is the value at the POPPED register
    //     WritePushPop(Command.C_PUSH, "add", 0);
    // }

    // public void sub() {
    //     // 2 pops 1 push
    //     WritePushPop(Command.C_POP, "sub", 0 );
    //     out.println("D=M" + "\n"); // M is the value at the POPPED register
    //     WritePushPop(Command.C_POP, "sub", 0 );
    //     out.println("D=D-M" + "\n"); // D is the D selected on the previous M, M is the value at the POPPED register
    //     WritePushPop(Command.C_PUSH, "sub", 0);
    // }

    public void WritePushPop(Command command, String segment, int index) {
        String line = "";
        if (command == Command.C_PUSH) {
            // if constant, select the A register with index
            if (segment.equalsIgnoreCase("constant")) {
                line += "@" + index + "\n";
                line += "D=A" + "\n";
            } else if (segment.equalsIgnoreCase("local")) {
                line += "@LCL"  + "\n";
                line += "D=M"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "A=D+A" + "\n";
                line += "D=M"   + "\n";
            } else if (segment.equalsIgnoreCase("argument")) {
                line += "@ARG"  + "\n";
                line += "D=M"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "A=D+A" + "\n";
                line += "D=M"   + "\n";
            } else if (segment.equalsIgnoreCase("this")) {
                line += "@THIS" + "\n";
                line += "D=M"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "A=D+A" + "\n";
                line += "D=M"   + "\n";
            } else if (segment.equalsIgnoreCase("that")) {
                line += "@THAT" + "\n";
                line += "D=M"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "A=D+A" + "\n";
                line += "D=M"   + "\n";
            } else if (segment.equalsIgnoreCase("temp")) {
                line += "@R5"   + "\n";
                line += "D=A"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "A=D+A" + "\n";
                line += "D=M"   + "\n";
            } else if (segment.equalsIgnoreCase("pointer")) {
                if (index == 0) {
                    line += "@THIS" + "\n";
                } else {
                    line += "@THAT" + "\n";
                }
                line += "D=M"   + "\n";
            } else if (segment.equalsIgnoreCase("static")) {
                line += "@" + inputFile.replaceAll(".*/", "") + Integer.toString(index) + "\n";
                line += "D=M" + "\n";
            }
            // PUSH
            line += "@SP"   + "\n";
            line += "A=M"   + "\n";
            line += "M=D"   + "\n";
            line += "@SP"   + "\n";
            line += "M=M+1" + "\n"; // select the address at SP, and increment the value at SP
        } else if (command == Command.C_POP) {
            if (segment.equalsIgnoreCase("local")) {
                line += "@LCL" + "\n";
                line += "D=M"  + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "D=D+A" + "\n";
            } else if (segment.equalsIgnoreCase("argument")) {
                line += "@ARG" + "\n";
                line += "D=M"  + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "D=D+A" + "\n";
            } else if (segment.equalsIgnoreCase("this")) {
                line += "@THIS" + "\n";
                line += "D=M"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "D=D+A" + "\n";
            } else if (segment.equalsIgnoreCase("that")) {
                line += "@THAT" + "\n";
                line += "D=M"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "D=D+A" + "\n";
            } else if (segment.equalsIgnoreCase("temp")) {
                line += "@R5"   + "\n";
                line += "D=A"   + "\n";
                line += "@" + Integer.toString(index) + "\n";
                line += "D=D+A" + "\n";
            } else if (segment.equalsIgnoreCase("pointer")) {
                if (index == 0) {
                    line += "@THIS" + "\n";
                } else {
                    line += "@THAT" + "\n";
                }
                line += "D=A" + "\n";
            } else if (segment.equalsIgnoreCase("static")) {
                line += "@" + inputFile.replaceAll(".*/", "") + Integer.toString(index) + "\n";
                line += "D=A" + "\n";
            }
            // POP
            line += "@R13"  + "\n"; // temporarily use R13 to store the current value
            line += "M=D"   + "\n";
            line += "@SP"   + "\n";
            line += "M=M-1" + "\n";
            line += "A=M"   + "\n"; // select the previous item in the stack
            line += "D=M"   + "\n";
            line += "@R13"  + "\n";
            line += "A=M"   + "\n";
            line += "M=D"   + "\n";
        }
        out.println(line);
    }

    public void Close() {
        if (out != null)
            out.println("(END)");
            out.println("@END");
            out.println("0;JMP");
            out.close();
    }
}
