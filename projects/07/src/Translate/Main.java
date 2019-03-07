public class Main {
    CodeWriter codeWriter = new CodeWriter();
    public static void main(String[] args) {
        Main m = new Main();
        // m.translate(args[1]);
        m.translate("SimpleAdd.vm");
    }
    public void translate(String files) {
        // assume single file first
        // implement directory later
        Parser parser = new Parser(files);
        while (parser.hasMoreCommands()) {
            parser.advance(); // do we need to advance the first time?
            System.out.println(parser.getCurrentCommand());
            codeWriter.setFileName(files);
            if (parser.commandType() == Command.C_PUSH) {
                System.out.println("push");
                codeWriter.WritePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            } else if (parser.commandType() == Command.C_ARITHMETIC) {
                System.out.println("arithmetic");
                codeWriter.writeArithmetic(parser.getCurrentCommand());
            }
        }
        codeWriter.Close();

    }
}
