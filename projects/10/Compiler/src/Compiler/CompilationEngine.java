package Compiler;

import jdk.nashorn.internal.parser.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CompilationEngine {
    PrintWriter out;
    JackTokenizer jt;
    public CompilationEngine(PrintWriter p, JackTokenizer jt) {
        this.out = p;
        this.jt = jt;
        // try {
        //     out = new PrintWriter(new BufferedWriter(new FileWriter("MyProg.xml")));
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
       CompileClass();
    }

    public void CompileClass() {
        // JackTokenizer jt = new JackTokenizer("MyProg.jack");
        out.println("<class>");
        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord() == "class") {
            out.println("  <keyword> " + jt.keyWord() + " </keyword>");
            jt.advance();
        } else {
            // the first token is not a keyword
            return;
        }

        // if (jt.tokenType() == TokenType.IDENTIFIER) {
        //     out.println("  <identifier> " + jt.identifier() + " </identifier>");
        //     jt.advance();
        // } else {
        //     // wrong structure;
        // }
        printIdentifier();

        if (jt.tokenType() == TokenType.SYMBOL) {
            out.println("  <symbol> " + jt.symbol() + " </symbol>");
            jt.advance();
        } else {
            // wrong structure;
        }

        // a classVarDec* subroutineDec* follows
        // put this while inside CompileClassVarDec
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("static|field")) {
            CompileClassVarDec();
            jt.advance();
        }

        // put this while inside CompileSubroutine
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("constructor|function|method")) {
            CompileSubroutine();
            jt.advance();
        }




        // while (jt.hasMoreTokens()) {
        //     jt.advance();
        //     if (jt.tokenType() == TokenType.KEYWORD) {
        //         // call the program structure variables based on which keyword it is
        //     } else if (jt.tokenType() == TokenType.IDENTIFIER) {

        //     } else if (jt.tokenType() == TokenType.SYMBOL) {

        //     } else if (jt.tokenType() == TokenType.STRING_CONST) {

        //     } else if (jt.tokenType() == TokenType.INT_CONST) {

        //     }
        // }
        out.println("</class>");
        out.close();
    }

    public void CompileClassVarDec() {
        out.println("  <keyword> " + jt.keyWord() + " </keyword>");
        jt.advance();

        varType();

        // several variables could be listed separated by commas
        do {
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                out.println("  <symbol>" + jt.symbol() + " </symbol>");
                jt.advance();
            } // else { // moved this outside the loop better
            //     // symbol should be ';'
            //     out.println("  <symbol>" + jt.symbol() + " </symbol>");
            // }
            // if (jt.tokenType() == TokenType.IDENTIFIER) {
            //     out.println("  <identifier>" + jt.identifier() + " </identifier>");
            //     jt.advance();
            // } else {
            //     // wrong structure
            // }
            printIdentifier();
        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');

        // this ends with ';'
        if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ';') {
            out.println("  <symbol>" + jt.symbol() + " </symbol>");
        }

    }

    public void varType() {
        // what type is it? Still need to take care of a type 'className'
        // if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("int|char|boolean")) {
        // void takes care of subroutine types!
        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("int|char|boolean|void")) {
            out.println("  <keyword>" + jt.keyWord() + " </keyword>");
            jt.advance();
        } else {
            // this must be the className
            printIdentifier();
            // wrong structure
        }
    }

    public void printIdentifier() {
        if (jt.tokenType() == TokenType.IDENTIFIER) {
            out.println("  <identifier>" + jt.identifier() + " </identifier>");
            jt.advance();
        } else {
            // wrong structure, error out: identifier expected
        }
    }

    public void CompileSubroutine() {
        // if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().equals("void")) {
        // }
        varType();
    }

    public void CompileParameterList() {

    }

    public void compileVarDec() {

    }

    public void compileStatements() {

    }

    public void compileDo() {

    }

    public void compileLet() {

    }

    public void compileWhile() {

    }

    public void compileReturn() {

    }

    public void compileIf() {

    }

    public void CompileExpression() {

    }

    public void CompileTerm() {

    }

    public void CompileExpressionList() {

    }
}
