package Compiler;

import java.io.*;
import java.nio.Buffer;

public class JackAnalyzer {
    public static void main(String[] args) {
        try {
            // Create a JackTokenizer from the Xxx.jack input file
            System.out.println(args[0]);
            // System.out.println(args[1]);
            if (!(args[0].endsWith(".jack"))) {
                System.err.println("Invalid File Name: " + args[0]);
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                JackTokenizer jt = new JackTokenizer(reader);
                // Create an output file called Xxx.xml and prepare it for writing
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(args[0] + ".xml")));
                // Use the CompilationEngine to compile the input JackTokenizer into the output file
                CompilationEngine ce = new CompilationEngine(out, jt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
