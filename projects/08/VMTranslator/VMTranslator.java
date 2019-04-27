// import java.util.List;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.File;
import java.io.IOException;

public class VMTranslator {
    CodeWriter codeWriter; // = new CodeWriter();
    public static void main(String[] args) {
        VMTranslator vt = new VMTranslator();
        try {
            if (args[0].endsWith(".vm")) {
                vt.codeWriter = new CodeWriter(args[0]);
                vt.translate(args[0]);
                System.out.println("Done translating " + args[0]);
            } else {
                File dir = new File(args[0]);
                File[] files = dir.listFiles();
                vt.codeWriter = new CodeWriter(args[0]);
                if (files != null) {
                    for (File file : files) {
                        vt.codeWriter.setFileName(file.getCanonicalPath());
                        if (file.getCanonicalPath().endsWith(".vm")) {
                            vt.translate(file.getCanonicalPath());
                            System.out.println("Done translating " + file.getCanonicalPath());
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //vt.translate("SimpleAdd.vm");
    }
    public void translate(String file) {
        // ArrayList<String> list = new ArrayList<String>();
        // assume single file first
        // implement directory later
        // try {
        //     File file = new File(files);
        //     if (file.isDirectory()) {
        //         for (File f : file.listFiles()) {
        //             if (f.getCanonicalPath().endsWith(".vm"))
        //                 list.add(f.getCanonicalPath());
        //         }
        //     } else {
        //         list.add(files);
        //     }
        //     for (String s : list) {
                // Parser parser = new Parser(files);
                // codeWriter = new CodeWriter(file, source);
                Parser parser = new Parser(file);
                while (parser.hasMoreCommands()) {
                    parser.advance(); // do we need to advance the first time?
//                    System.out.println(parser.getCurrentCommand());
                    // codeWriter.setFileName(files);
                    // codeWriter.setFileName(file);
                    if (parser.commandType() == Command.C_PUSH || 
                        parser.commandType() == Command.C_POP) {
//                        System.out.println("push");
                        codeWriter.WritePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    } else if (parser.commandType() == Command.C_ARITHMETIC) {
//                        System.out.println("arithmetic");
                        codeWriter.writeArithmetic(parser.getCurrentCommand());
                    } else if (parser.commandType() == Command.C_LABEL) {
//                        System.out.println("label");
                        codeWriter.writeLabel(parser.arg1());
                    } else if (parser.commandType() == Command.C_GOTO) {
//                        System.out.println("goto");
                        codeWriter.writeGoto(parser.arg1());
                    } else if (parser.commandType() == Command.C_IF) {
//                        System.out.println("if");
                        codeWriter.writeIf(parser.arg1());
                    } else if (parser.commandType() == Command.C_FUNCTION) {
//                        System.out.println("function");
                        codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    } else if (parser.commandType() == Command.C_RETURN) {
//                        System.out.println("return");
                        codeWriter.writeReturn();
                    } else if (parser.commandType() == Command.C_CALL) {
//                        System.out.println("call");
                        codeWriter.writeCall(parser.arg1(), parser.arg2());
                    }
                }
                codeWriter.Close();
            // }

        // } catch (IOException e) {
        //     System.err.format("Exception: %s%n", e);
        // }
    }
}
