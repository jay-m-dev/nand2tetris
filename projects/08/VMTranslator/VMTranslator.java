public class VMTranslator {
    CodeWriter codeWriter = new CodeWriter();
    public static void main(String[] args) {
        VMTranslator vt = new VMTranslator();
        vt.translate(args[0]);
        //vt.translate("SimpleAdd.vm");
    }
    public void translate(String files) {
        // assume single file first
        // implement directory later
        Parser parser = new Parser(files);
        while (parser.hasMoreCommands()) {
            parser.advance(); // do we need to advance the first time?
            System.out.println(parser.getCurrentCommand());
            codeWriter.setFileName(files);
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
}
