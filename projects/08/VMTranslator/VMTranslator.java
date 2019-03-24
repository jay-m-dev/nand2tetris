import java.io.File;
import java.util.ArrayList;
import java.io.IOException;
// import java.util.List;

public class VMTranslator {
    CodeWriter codeWriter = new CodeWriter();
    public static void main(String[] args) {
        VMTranslator vt = new VMTranslator();
        vt.translate(args[0]);
        //vt.translate("SimpleAdd.vm");
    }
    public void translate(String files) {
        ArrayList<String> list = new ArrayList<String>();
        // assume single file first
        // implement directory later
        try {
            File file = new File(files);
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if (f.getCanonicalPath().toString().endsWith(".vm"))
                        list.add(f.getCanonicalPath().toString());
                }
            } else {
                list.add(files);
            }
            for (String s : list) {
                // Parser parser = new Parser(files);
                Parser parser = new Parser(s);
                while (parser.hasMoreCommands()) {
                    parser.advance(); // do we need to advance the first time?
                    System.out.println(parser.getCurrentCommand());
                    // codeWriter.setFileName(files);
                    codeWriter.setFileName(s);
                    if (parser.commandType() == Command.C_PUSH || 
                        parser.commandType() == Command.C_POP) {
                        System.out.println("push");
                        codeWriter.WritePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    } else if (parser.commandType() == Command.C_ARITHMETIC) {
                        System.out.println("arithmetic");
                        codeWriter.writeArithmetic(parser.getCurrentCommand());
                    } else if (parser.commandType() == Command.C_LABEL) {
                        System.out.println("label");
                        codeWriter.writeLabel(parser.arg1());
                    } else if (parser.commandType() == Command.C_GOTO) {
                        System.out.println("goto");
                        codeWriter.writeGoto(parser.arg1());
                    } else if (parser.commandType() == Command.C_IF) {
                        System.out.println("if");
                        codeWriter.writeIf(parser.arg1());
                    } else if (parser.commandType() == Command.C_FUNCTION) {
                        System.out.println("function");
                        codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    } else if (parser.commandType() == Command.C_RETURN) {
                        System.out.println("return");
                        codeWriter.writeReturn();
                    } else if (parser.commandType() == Command.C_CALL) {
                        System.out.println("call");
                        codeWriter.writeCall(parser.arg1(), parser.arg2());
                    }
                }
                codeWriter.Close();
            }

        } catch (IOException e) {
            System.err.format("Exception: %s%n", e);
        }
    }
}
