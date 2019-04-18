package Compiler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CompilationEngine {
    PrintWriter out;
    public CompilationEngine(PrintWriter p) {
        this.out = p;
        // try {
        //     out = new PrintWriter(new BufferedWriter(new FileWriter("MyProg.xml")));
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        CompileClass();
    }

    public void CompileClass() {
        JackTokenizer jt = new JackTokenizer("MyProg.jack");
        out.println("<class>");
        while (jt.hasMoreTokens()) {
            if (jt.tokenType() == TokenType.KEYWORD) {

            } else if (jt.tokenType() == TokenType.IDENTIFIER) {

            } else if (jt.tokenType() == TokenType.SYMBOL) {

            } else if (jt.tokenType() == TokenType.STRING_CONST) {

            } else if (jt.tokenType() == TokenType.INT_CONST) {

            }
        }
        out.println("</class>");
    }

    public void CompileClassVarDec() {

    }

    public void CompileSubroutine() {

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
