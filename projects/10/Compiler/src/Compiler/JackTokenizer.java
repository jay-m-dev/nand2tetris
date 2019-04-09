package Compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class JackTokenizer {
    JackTokenizer jt;
    private BufferedReader inputStream;
    private String currentToken;
    private String[] tokens;
    private Queue<String> queue = new LinkedList<>();
    public JackTokenizer(String fileName) {
        String line;
        try {
            inputStream = new BufferedReader(new FileReader(fileName));
            while ((line = inputStream.readLine()) != null) {
                // remove empty lines and comments
                line = line.trim();
                if (line.startsWith("//") || line.isEmpty())
                    continue;
                if (line.contains("//"))
                    line = line.split("//")[0].trim();
                // need to get rid of /* */ and /** */ comments
                // tokenize the line and add it to the queue
                // if the line contains a String constant (" ")
                // then just add it to the queue
                // tokens are not gonna work. Need to analyze each line
                // character by character
                // not every token is separated by spaces
                // tokens = line.split(" ");
                // for (String s : tokens) {
                //     s = s.trim();
                //     if (!s.isEmpty()) {
                //         queue.add(s);
                //     }
                // }
                lineTokenizer(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lineTokenizer(String s) {
        String word = "";
        String stringConstant = "";
        String integerConstant = "";
        Boolean string = false;
        Boolean integer = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (string) {
                stringConstant += c;
            } else if (integer && !isInteger(String.valueOf(c))) {
                // integer is true, but the current character c is not longer an integer
                // let's add the value of integerContant to the queue
                queue.add(integerConstant);
                // let's reset
                integer = false;
                integerConstant = "";
            } else if ("\"".indexOf(c) != -1) {
                // this is the beginning of a String constant
                // build the string constant
                // the first " will set this string to true, the next string will set this to false
                string = (string) ? false : true; // toggle between true and false
                stringConstant += c;
                if (!string) { // if string is false, that means we reached the end of the string
                    // add this string to the queue before resetting it
                    queue.add(stringConstant);
                    stringConstant = ""; // reset value
                }
            } else if ("()[]{},;=.+-*/&|~<>".indexOf(c) != -1) {
                queue.add(String.valueOf(c));
            } else if (isInteger(String.valueOf(c))) {
                // need to store Boolean integer just to reset
                integer = true;
                // integer = (integer) ? false : true; // toggle between true and false
                integerConstant += c;
            } else if (" ".indexOf(c) != -1) {
                // ignore white spaces, the case of the space being inside quotes is already taken care of.
            } else {
                // this is probably a reserved word or identifier
                // iterate till we find the next space
                while (" ".indexOf(c) == 1) {
                    word += c;
                    i++;
                }
                i--; // let's return to the right index
                // we can add this word to queue without caring whether it is a reserved word or an identifier
                // the tokenType method will take care of distinguishing which type it is.
                queue.add(word);
                word = ""; // reset this
            }
        }
    }

    public Boolean hasMoreTokens() { return (queue.peek() != null); }

    public void advance() { currentToken = queue.poll(); }

    public TokenType tokenType() {
        if (currentToken.matches("class|constructor|method|function|"
                                    + "int|boolean|char|void|"
                                    + "var|static|field|"
                                    + "let|do|if|else|while|return|"
                                    + "true|false|null|"
                                    + "this")) {
            return TokenType.KEYWORD;
        } else if ("()[]{},;=.+-*/&|~<>".indexOf(currentToken) != -1) {
            return TokenType.SYMBOL;
        } else if (isInteger(currentToken)) {
            return TokenType.INT_CONST;
        } else if (currentToken.startsWith("\"")) {
            return TokenType.STRING_CONST;
        } else {
            return TokenType.IDENTIFIER;
        }
    }

    // don't think I need keywordType
    public String keyWord() {
        return currentToken.toUpperCase();
    }

    public char symbol() {
        return currentToken.charAt(0);
    }

    public String identifier() {
        return currentToken;
    }

    public int intVal() {
        return Integer.parseInt(currentToken);
    }

    public String stringVal() {
        return currentToken;
    }

    private Boolean isInteger(String s) {
        try {
            Integer i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
