package Compiler;

import java.io.*;

public class JackAnalyzer {
    public static void main(String[] args) {
        try {
            // Create a JackTokenizer from the Xxx.jack input file
            BufferedReader reader = new BufferedReader(new FileReader(args[1]));
            JackTokenizer jt = new JackTokenizer(reader);
            // Create an output file called Xxx.xml and prepare it for writing
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("MyProg.xml")));
            // Use the CompilationEngine to compile the input JackTokenizer into the output file
            CompilationEngine ce = new CompilationEngine(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
