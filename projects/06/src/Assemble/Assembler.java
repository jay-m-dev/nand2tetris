package Assemble;

import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// import Parser.Command;

class Assembler {
    public static void main(String[] args) {
        if (args != null && args.length > 0) 
            assemble(args[0]);
    }
    public static void assemble(String fileName) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("MyProg.hack")));
            Parser parser = new Parser(fileName);
            String line;
            Code code = new Code();
            while (parser.hasMoreCommands()) {
                line = "";
                parser.advance();
                if (parser.commandType() == Command.C_COMMAND) {
                    line += "111";
                    line += code.comp(parser.comp());
                    line += code.dest(parser.dest());
                    line += code.jump(parser.jump());
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