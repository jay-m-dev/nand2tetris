package Compiler;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class JackTokenizer {
    JackTokenizer jt;
    StreamTokenizer st;
    private Reader inputStream;
    private String currentToken;
    private String[] tokens;
    private Queue<String> queue = new LinkedList<>();

    public JackTokenizer(Reader reader) {
        try {
            int token;
            st = new StreamTokenizer(reader);
            st.ordinaryChar('.');
            st.ordinaryChar('/');
            st.slashSlashComments(true);
            st.slashStarComments(true);
            st.quoteChar('"');
            //System.out.println(st);
            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                if (st.ttype == '"' || st.ttype == StreamTokenizer.TT_WORD) {
                    queue.add(st.sval);
//                    System.out.println("word:" + st.sval);
                }
                else if (st.ttype == StreamTokenizer.TT_NUMBER) {
                    queue.add(Double.toString(st.nval));
//                    System.out.println("number:" + st.nval);
                }
                else {
                    queue.add(Character.toString((char) token));
//                    System.out.println("string token:" + Character.toString((char) token));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // iterate over st and work with Jack-specific keywords and syntax
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
        } else if ("{}()[].,;+-*/&|<>=~".contains(currentToken)) {
        // } else if (currentToken.matches("()\[{},;=+*|/&~<>\]-")) {
        // } else if (currentToken.contains("{}()[].,;+-*/&|<>=~")) {
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
        return currentToken;
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
