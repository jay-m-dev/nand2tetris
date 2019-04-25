package Compiler;

// import jdk.nashorn.internal.parser.Token;

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
        compileClass();
        // jt.advance();
        // while (this.jt.hasMoreTokens()) {
        //     if (jt.tokenType() == TokenType.SYMBOL)
        //         System.out.println("symbol:" + jt.symbol());
        //     else if (jt.tokenType() == TokenType.KEYWORD)
        //         System.out.println("keyword:" + jt.keyWord());
        //     else if (jt.tokenType() == TokenType.IDENTIFIER)
        //         System.out.println("identifier:" + jt.identifier());
        //     else if (jt.tokenType() == TokenType.STRING_CONST)
        //         System.out.println("string:" + jt.stringVal());
        //     else if (jt.tokenType() == TokenType.INT_CONST)
        //         System.out.println("integer:" + jt.intVal());

        //     jt.advance();
        // }
    }

    /* symbols:
        'sss': tokens verbatim ("terminals")
          sss: names of language constructs ("non-terminals")
           (): used for grouping language constructs
          x|y: either x or y can appear
           x?: x appears 0 or 1 times
           x*: x appears 0 or more times
    */

    /* 'class' className '{' classVarDec* subroutineDec* '}' */
    public void compileClass() {
        out.println("<class>");
        int level = 1;
        jt.advance();
        System.out.println("in compileClass 1:" + jt.keyWord() + "###");
        // 'class' keyword
        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().equals("class")) {
            System.out.println("in compileClass 2:" + jt.keyWord());
            printKeyword(level);
        }
        // className
        printIdentifier(level);
        // '{'
        printSymbol(level);
        // classVarDec*
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("static|field")) {
            compileClassVarDec(level);
        }
        // subroutineDec*
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("constructor|function|method")) {
            compileSubroutine(level);
        }
        // '}'
        printSymbol(level);
        out.println("</class>");
        out.close();
    }

    /* ('static' | 'field') type varName(',' varName)* ';' */
    public void compileClassVarDec(int level) {
        out.println(indent(level) + "<classVarDec> ");
        // ('static' | 'field')
        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("static|field")) {
            printKeyword(level + 1);
        }
        // type
        printType(level + 1);
        // varName(',' varName)*
        do {
            // ',', should not be ',' at the first iteration
            // if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                printSymbol(level + 1);
            }
            // varName
            printIdentifier(level + 1);
            // if ',' follows the identifier, keep iterating
        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');

        // ';'
        // assumes the previous loop exited by printing an identifier and not the ',' symbol
        printSymbol(level + 1);

        out.println(indent(level) + "</classVarDec>");
    }

    /* ('constructor' | 'function' | 'method') ('void' | type) subroutineName '(' parameterList ')' subroutineBody */
    public void compileSubroutine(int level) {
        out.println(indent(level) + "<subroutineDec> ");
//        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("constructor|function|method")) {
            // if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().equals("void")) {
            // }
            // ('constructor' | 'function' | 'method')
            printKeyword(level + 1);
            // ('void' | type)
            printType(level + 1);
            // subroutineName
            printIdentifier(level + 1);
            // '('
            printSymbol(level + 1);
            // '(' parameterList ')'
            compileParameterList(level + 1);
            // ')'
            printSymbol(level + 1);

//        }
        compileSubroutineBody(level + 1);
        out.println(indent(level) + "</subroutineDec>");
    }

    /* '{' varDec* statements '}' */
    public void compileSubroutineBody(int level) {
        out.println(indent(level) + "<subroutineBody>");
        // '{'
        printSymbol(level + 1);
        // varDec*
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().equals("var")) {
            compileVarDec(level + 1);
        }
        // statements
        compileStatements(level + 1);
        // '}'
        printSymbol(level + 1);

        out.println(indent(level) + "</subroutineBody>");
    }

    /* statements* */
    public void compileStatements(int level) {
        out.println(indent(level) + "<statements>");
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("let|if|while|do|return")) {
            if (jt.tokenType() == TokenType.KEYWORD) {
                switch(jt.keyWord()) {
                    case "let":
                        compileLet(level + 2);
                        break;
                    case "if":
                        compileIf(level + 2);
                        break;
                    case "while":
                        compileWhile(level + 2);
                        break;
                    case "do":
                        compileDo(level + 2);
                        break;
                    case "return":
                        compileReturn(level + 2);
                        break;
                }
            }
        }

        out.println(indent(level) + "</statements>");
    }

    /* 'var' type varName(',' varName)* ';' */
    public void compileVarDec(int level) {
        out.println(indent(level) + "<varDec>");
        if (!(jt.tokenType() == TokenType.KEYWORD && jt.keyWord().equals("var"))) {
            // no variables found
            out.println(indent(level) + "</varDec>");
            return;
        }
        // 'var'
        printKeyword(level + 1);
        do {
            // ',', should not be ',' at the first iteration
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                printSymbol(level + 1);
            }
            // type
            printType(level + 1);
            // varName
            printIdentifier(level + 1);
        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');
        // ';'
        printSymbol(level + 1);

        out.println(indent(level) + "</varDec>");
    }

    /* ((type varName)(',' type varName)*)? */
    public void compileParameterList(int level) {
        out.println(indent(level) + "<parameterList>");
        // if there are parameters, the first token type will be a Keyword or an Identifier (varName = className)
        if (!(jt.tokenType() == TokenType.KEYWORD || jt.tokenType() == TokenType.IDENTIFIER)) {
            out.println(indent(level) + "</parameterList>");
            return;
        }
        do {
            // ',', should not be ',' at the first iteration
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                // might not need the if here, printSymbol will not print if the tokenType is not a symbol!
                printSymbol(level + 1);
            }
            // type
            printType(level + 1);
            // varName
            printIdentifier(level + 1);

        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');

        out.println(indent(level) + "</parameterList>");
    }

    /* term (op term)* */
    public void compileExpression(int level) {
        out.println(indent(level-1) + "<expression>");
        // term
        printTerm(level);

        // (op term)*
//        while (jt.tokenType() == TokenType.SYMBOL && (jt.symbol() == '+' || jt.symbol() == '-' || jt.symbol() == '*' || jt.symbol() == '/' || jt.symbol() == '&' || jt.symbol() == '|' || jt.symbol() == '<' || jt.symbol() == '>' || jt.symbol() == '=')) {
        while (jt.tokenType() == TokenType.SYMBOL && isOperator(jt.symbol())) {
            // op symbol
            printSymbol(level);
            // term
            printTerm(level);
            // pointer should be advanced to the next token
        }


        out.println(indent(level-1) + "</expression>");
    }

    private boolean isOperator(char c) {
        char[] op = {'+', '-', '*', '/', '&', '|', '<', '>', '=', '~'};
        for (char c1 : op) {
            if (c == c1) {
                return true;
            }
        }
        return false;
    }

    /* integerConstant|stringConstant|keywordConstant|varName|varName '[' expression ']'|subroutineCall|
        '(' expression ')'|unaryOp term */
    private void printTerm(int level) {
        out.println(indent(level) + "<term>");
        if (jt.tokenType() == TokenType.INT_CONST) {
            out.println(indent(level + 1) + "<intConstant> " + jt.intVal() + " </intConstant>");
            jt.advance();
        } else if (jt.tokenType() == TokenType.STRING_CONST) {
            out.println(indent(level + 1) + "<stringConstant> " + jt.stringVal() + " </stringConstant>");
            jt.advance();
        } else if (jt.tokenType() == TokenType.KEYWORD) {
            printKeyword(level + 1);
        } else if (jt.tokenType() == TokenType.IDENTIFIER) {
            // varName|varName '[' expression ']'|subroutineCall
            printIdentifier(level + 1); ///1 SquareGame
            if (jt.tokenType() == TokenType.SYMBOL) {
                if (jt.symbol() == '[') {
                    // '['
                    printSymbol(level + 1);
                    // expression
                    compileExpression(level + 1);
                    // ']'
                    printSymbol(level + 1);
                } else if (jt.symbol() == '.') {
                    // subroutineCall
                    compileSubroutineCall(level + 1); ///2 '.'
                }
            }
        } else if (jt.tokenType() == TokenType.SYMBOL) {
            // '(' expression ')'|unaryOp term
            if (jt.symbol() == '(') {
                // compileExpression takes care of '(' and ')'
                // '('
                printSymbol(level + 1);
                // expression
                compileExpression(level + 1);
                // ')'
                printSymbol(level + 1);
            } else if (jt.symbol() == '-' || jt.symbol() == '~') {
                // unaryOp
                printSymbol(level + 1);
                // term
                printType(level + 1);
            }
        }
        out.println(indent(level) + "</term>");

    }

    /* subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')' */
    public void compileSubroutineCall(int level) {
        // out.println(indent(level-1) + "<subroutineCall>");
        // subroutineName already printed by caller
        if (jt.tokenType() == TokenType.IDENTIFIER) {
            printIdentifier(level);
        }
        // symbol can be '(' (for subroutineName) or '.' (for className|varName)
        if (jt.symbol() == '(') {
            printSymbol(level);
            // expressionList
            compileExpressionList(level);
            // ')'
            printSymbol(level);
        } else if (jt.symbol() == '.') {
            printSymbol(level);
            // subroutineName
            printIdentifier(level);
            // '('
            printSymbol(level);
            // expressionList
            compileExpressionList(level);
            // ')'
            printSymbol(level);
            // '(' expressionList ')'
            // compileSubroutineCall(level);
        }

        // out.println(indent(level-1) + "</subroutineCall>");

    }

    /* (expression (',' expression)*)? */
    public void compileExpressionList(int level) {
        out.println(indent(level) + "<expressionList>");
        do {
            // ',' should not be there at the first iteration
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                // printSymbol(7);
                printSymbol(level + 1);
            }
            compileExpression(level + 2); // indented one more than expression
        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');
        out.println(indent(level) + "</expressionList>");
    }

    /* 'do' subroutineCall ';' */
    public void compileDo(int level) {
        out.println(indent(level-1) + "<doStatement>");
        // 'do'
        printKeyword(level);
        // subroutineCall
        compileSubroutineCall(level);
        // ';'
        printSymbol(level);

        out.println(indent(level-1) + "</doStatement>");
    }

    /* 'let' varName ('[' expression ']')? '=' expression ';' */
    public void compileLet(int level) {
        // out.println("        <letStatement>");
        out.println(indent(level-1) + "<letStatement>");
        // 'let'
        printKeyword(level);
        // varName
        printIdentifier(level);
        // '['
        if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == '[') {
            printSymbol(level);
            // expression
            compileExpression(level + 1);
            // ']'
            printSymbol(level);
        }
        // '='
        printSymbol(level);
        // expression
        compileExpression(level + 1);
        // ';'
        System.out.println("should be ; " + jt.symbol());
        printSymbol(level);
        out.println(indent(level-1) + "</letStatement>");
    }

    /* 'while' '(' expression ')' '{' statements '}' */
    public void compileWhile(int level) {
        out.println(indent(level-1) + "<whileStatement>");
        // 'while'
        printKeyword(level);
        // '('
        printSymbol(level);
        // expression
        compileExpression(level + 1);
        // ')'
        printSymbol(level);
        // '{'
        printSymbol(level);
        // statements
        compileStatements(level);
        // '}'
        printSymbol(level);

        out.println(indent(level-1) + "</whileStatement>");
    }

    /* 'return' expression? ';' */
    public void compileReturn(int level) {
        out.println(indent(level-1) + "<returnStatement>");
        // 'return'
        printKeyword(level);
        // expression (if any)
        if (!(jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ';')) {
            compileExpression(level + 1);
        }
        // ';'
        printSymbol(level);

        out.println(indent(level-1) + "</returnStatement>");
    }

    /* 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}' */
    public void compileIf(int level) {
        out.println(indent(level-1) + "<ifStatement>");
        // 'if'
        printKeyword(level);
        // '('
        printSymbol(level);
        // expression
        compileExpression(level + 1);
        // ')'
        printSymbol(level);
        // '{'
        printSymbol(level);
        // statements
        compileStatements(level);
        // '}'
        printSymbol(level);
        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().equals("else")) {
            // 'else'
            printKeyword(level);
            // '{'
            printSymbol(level);
            // statements
            compileStatements(level);
            // '}'
            printSymbol(level);
        }

        out.println(indent(level-1) + "</ifStatement>");
    }

    private void printType(int level) {
        // level is passed down

        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("int|char|boolean|void")) {
            printKeyword(level);
        } else {
            printIdentifier(level);
            // wrong structure
        }
    }

    private String indent(int level) {
        String indent = "  ";
        //indent = indent.repeat(level);
        // if (level == 2)
        //     indent += indent; // double the indentation level
        // else if (level == 3)
        //     indent += indent + indent;
        // else if (level == 4)
        //     indent += indent + indent + indent;
        // else if (level == 5)
        //     indent += indent + indent + indent + indent;
        // else if (level == 6)
        //     indent += indent + indent + indent + indent + indent;

        return indent.repeat(level);
    }

    private void printKeyword(int level) {
        if (jt.tokenType() == TokenType.KEYWORD) {
            System.out.println("in printKeyword:" + jt.keyWord());
            out.println(indent(level) + "<keyword> " + jt.keyWord() + " </keyword>");
            jt.advance();
        } else {
            // error out: keyword expected
        }
    }

    private void printSymbol(int level) {
        System.out.println("printSymbol type: " + jt.tokenType());
        if (jt.tokenType() == TokenType.SYMBOL) {
            String symbol;
            if (jt.symbol() == '<')
                symbol = "&lt;";
            else if (jt.symbol() == '>')
                symbol = "&gt;";
            else if (jt.symbol() == '&')
                symbol = "&amp;";
            else
                symbol = Character.toString(jt.symbol());

            out.println(indent(level) + "<symbol> " + symbol + " </symbol>");
            jt.advance();
        } else {
            // wrong structure; error out: symbol expected
        }
    }

    private void printIdentifier(int level) {
        if (jt.tokenType() == TokenType.IDENTIFIER) {
            out.println(indent(level) + "<identifier> " + jt.identifier() + " </identifier>");
            jt.advance();
        } else {
            // wrong structure, error out: identifier expected
        }
    }
}
