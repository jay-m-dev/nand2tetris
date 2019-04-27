// package Assemble;

import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class Assembler {
    private static SymbolTable st = new SymbolTable();
    public static void main(String[] args) {
        if (args != null && args.length > 0)  {
            if (args[0].endsWith(".asm")) {
                buildSymbolTable(args[0]);
                assemble(args[0]);
                System.out.println("Done assembling " + args[0]);
            } else {
                System.out.println("Invalid file. Must be a '.asm' file.");
            }
        }
    }
    public static void buildSymbolTable(String fileName) {
        // first pass to take care of labels (Xxx)
        Parser parser = new Parser(fileName);
        int lineCount = 0;
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.commandType() == Command.L_COMMAND) {
                // add this symbol and ROM address to the symbol table
                st.addEntry(parser.symbol(), lineCount);
            } else {
                lineCount++;
            }
        }
    }
    public static void assemble(String fileName) {
        try {
            String outFile = fileName.substring(0, fileName.indexOf(".")) + ".hack";
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            Parser parser = new Parser(fileName);
            String tempAddress;
            String line;
            int newAddress = 16;
            Code code = new Code();
            while (parser.hasMoreCommands()) {
                line = "";
                parser.advance();
                if (parser.commandType() == Command.C_COMMAND) {
                    line += "111";
                    line += code.comp(parser.comp());
                    line += code.dest(parser.dest());
                    line += code.jump(parser.jump());
                } else if (parser.commandType() == Command.L_COMMAND) {
                    // don't translate L_COMMANDs (Xxx)
                    continue;
                } else if (parser.commandType() == Command.A_COMMAND && !parser.isNumeric(parser.symbol())) {
                    // an A_command will start with @, not a numeric symbol means this is a label
                    line += "0";
                    if (!(st.contains(parser.symbol()))) {
                        st.addEntry(parser.symbol(), newAddress);
                        newAddress++;
                    }

                    tempAddress = Integer.toBinaryString(st.getAddress(parser.symbol()));
                    line += tempAddress.format("%15s", tempAddress).replace(' ', '0');

                } else {
                    line += "0";
                    line += parser.symbol();
                }
                out.println(line);
            }
            out.close();
        } catch (IOException e) {
            System.err.format("Exception: %s%n", e);
        }
    }
}
