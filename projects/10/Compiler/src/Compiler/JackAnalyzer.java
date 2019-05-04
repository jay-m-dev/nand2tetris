package Compiler;

import java.io.*;
import java.nio.Buffer;

public class JackAnalyzer {
    public static void main(String[] args) {
        try {
            // Create a JackTokenizer from the Xxx.jack input file
            // System.out.println(args[1]);
            if (!(args[0].endsWith(".jack"))) {
                File dir = new File(args[0]);
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        process(file.getCanonicalPath());
                        System.out.println("Done analyzing " + file.getCanonicalPath());
                    }
                }
                // System.err.println("Invalid File Name: " + args[0]);
            } else {
                process(args[0]);
                System.out.println("Done analyzing " + args[0]);
                // BufferedReader reader = new BufferedReader(new FileReader(args[0]));
                // JackTokenizer jt = new JackTokenizer(reader);
                // // Create an output file called Xxx.xml and prepare it for writing
                // String file = args[0].substring(0, args[0].indexOf('.'));
                // PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file + ".xml")));
                // // Use the CompilationEngine to compile the input JackTokenizer into the output file
                // CompilationEngine ce = new CompilationEngine(out, jt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void process(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        JackTokenizer jt = new JackTokenizer(reader);
        // Create an output file called Xxx.xml and prepare it for writing
        if (file.contains(".")) {
            file = file.substring(0, file.indexOf('.'));
        }
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file + ".xml")));
        // Use the CompilationEngine to compile the input JackTokenizer into the output file
        CompilationEngine ce = new CompilationEngine(out, jt);

    }
}
