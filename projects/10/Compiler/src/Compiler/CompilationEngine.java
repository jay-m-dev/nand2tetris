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
        // 'class' keyword
        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord() == "class") {
            printKeyword(1);
        }
        // className
        printIdentifier(1);
        // '{'
        printSymbol(1);
        // classVarDec*
        compileClassVarDec();
        // subroutineDec*
        compileSubroutine();
        // '}'
        printSymbol(1);
        out.println("</class>");
        out.close();
    }

    /* ('static' | 'field') type varName(',' varName)* ';' */
    public void compileClassVarDec() {
        out.println("  <classVarDec> ");
        // ('static' | 'field')
        if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("static|field")) {
            printKeyword(1);
        }
        // type
        printType(1);
        // varName(',' varName)*
        do {
            // ',', should not be ',' at the first iteration
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                printSymbol(1);
            }
            // varName
            printIdentifier(1);
            // if ',' follows the identifier, keep iterating
        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');

        // ';'
        // assumes the previous loop exited by printing an identifier and not the ',' symbol
        printSymbol(1);

        out.println("  </classVarDec>");
    }

    /* ('constructor' | 'function' | 'method') ('void' | type) subroutineName '(' parameterList ')' subroutineBody */
    public void compileSubroutine() {
        out.println("  <subroutineDec> ");
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("constructor|function|method")) {
            // if (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().equals("void")) {
            // }
            // ('constructor' | 'function' | 'method')
            printKeyword(2);
            // ('void' | type)
            printType(2);
            // subroutineName
            printIdentifier(2);
            // '('
            printSymbol(2);
            // '(' parameterList ')'
            compileParameterList();
            // ')'
            printSymbol(2);

        }
        out.println("  </subroutineDec>");
        compileSubroutineBody();
    }

    /* '{' varDec* statements '}' */
    public void compileSubroutineBody() {
        out.println("    <subroutineBody>");
        // '{'
        printSymbol(3);
        // varDec*
        compileVarDec();

        compileStatements();

        out.println("    </subroutineBody>");
    }

    /* statements* */
    public void compileStatements() {
        out.println("      <statements>");
        while (jt.tokenType() == TokenType.KEYWORD && jt.keyWord().matches("let|if|while|do|return")) {
            if (jt.tokenType() == TokenType.KEYWORD) {
                switch(jt.keyWord()) {
                    case "let":
                        compileLet(5);
                        break;
                    case "if":
                        compileIf(5);
                        break;
                    case "while":
                        compileWhile(5);
                        break;
                    case "do":
                        compileDo(5);
                        break;
                    case "return":
                        compileReturn();
                        break;
                }
            }
        }

        out.println("      </statements>");
    }

    /* 'var' type varName(',' varName)* ';' */
    public void compileVarDec() {
        out.println("      <varDec>");
        if (!(jt.tokenType() == TokenType.KEYWORD && jt.keyWord() == "var")) {
            // no variables found
            return;
        }
        // 'var'
        printKeyword(4);
        do {
            // ',', should not be ',' at the first iteration
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                printSymbol(1);
            }
            // type
            printType(4);
            // varName
            printIdentifier(4);
        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');

        out.println("      </varDec>");
    }

    /* ((type varName)(',' type varName)*)? */
    public void compileParameterList() {
        out.println("    <subroutineDec>");
        // if there are parameters, the first token type will be a Keyword or an Identifier (varName = className)
        if (!(jt.tokenType() == TokenType.KEYWORD || jt.tokenType() == TokenType.IDENTIFIER))
            return;
        do {
            // ',', should not be ',' at the first iteration
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                // might not need the if here, printSymbol will not print if the tokenType is not a symbol!
                printSymbol(3);
            }
            // type
            printType(3);
            // varName
            printIdentifier(3);

        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');

        out.println("    </subroutineDec>");
    }

    /* term (op term)* */
    public void compileExpression(int level) {
        out.println(indent(level-1) + "<expression>");
        // term
        printTerm(level);

        // (op term)*
        while (jt.tokenType() == TokenType.SYMBOL) {
            // op symbol
            printSymbol(level);
            // term
            printTerm(level);
            // pointer should be advanced to the next token
        }


        out.println(indent(level-1) + "</expression>");
    }

    /* integerConstant|stringConstant|keywordConstant|varName|varName '[' expression ']'|subroutineCall|
        '(' expression ')'|unaryOp term */
    private void printTerm(int level) {
        if (jt.tokenType() == TokenType.INT_CONST) {
            out.println(indent(level) + "<intConstant> " + jt.intVal() + " </intConstant>");
            jt.advance();
        } else if (jt.tokenType() == TokenType.STRING_CONST) {
            out.println(indent(level) + "<stringConstant> " + jt.stringVal() + " </stringConstant>");
            jt.advance();
        } else if (jt.tokenType() == TokenType.KEYWORD) {
            printKeyword(level);
        } else if (jt.tokenType() == TokenType.IDENTIFIER) {
            // varName|varName '[' expression ']'|subroutineCall
            printIdentifier(level); // could be varName|subroutineCall
            if (jt.tokenType() == TokenType.SYMBOL) {
                if (jt.symbol() == '[') {
                    // varName '['
                    printSymbol(level);
                } else if (jt.symbol() == '(') {
                    // subroutineCall
                    compileSubroutineCall(level);
                }
            }
        } else if (jt.tokenType() == TokenType.SYMBOL) {
            // '(' expression ')'|unaryOp term
            if (jt.symbol() == '(') {
                // '('
                printSymbol(level);
                // expression
                compileExpression(level);
                // ')'
                printSymbol(level);
            } else if (jt.symbol() == '-' || jt.symbol() == '~') {
                // unaryOp
                printSymbol(level);
                // term
                printType(level);
            }
        }

    }

    /* subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')' */
    public void compileSubroutineCall(int level) {
        // subroutineName already printed by caller
        // symbol can be '(' (for subroutineName) or '.' (for className|varName)
        if (jt.symbol() == '(') {
            printSymbol(level);
            // expressionList
            compileExpressionList();
            // ')'
            printSymbol(level);
        } else if (jt.symbol() == '.') {
            printSymbol(level);
            // subroutineName
            printIdentifier(level);
            // '(' expressionList ')'
            compileSubroutineCall(level);
        }

    }

    /* (expression (',' expression)*)? */
    public void compileExpressionList() {
        do {
            // ',' should not be there at the first iteration
            if (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',') {
                printSymbol(7);
            }
            compileExpression(7); // indented one more than expression
        } while (jt.tokenType() == TokenType.SYMBOL && jt.symbol() == ',');
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

        out.println(indent(level-1) + "<doStatement>");
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
            compileExpression(level);
            // ']'
            printSymbol(level);
        }
        // '='
        printSymbol(level);
        // expression
        compileExpression(level);
        // ';'
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
        compileExpression(level);
        // ')'
        printSymbol(level);
        // '{'
        printSymbol(level);
        // statements
        compileStatements();
        // '}'
        printSymbol(level);

        out.println(indent(level-1) + "<whileStatement>");
    }

    public void compileReturn() {

    }

    /* 'if' '(' expression ')' '{' statements '}' */
    public void compileIf(int level) {
        out.println(indent(level-1) + "<ifStatement>");
        // 'if'
        printKeyword(level);
        // '('
        printSymbol(level);
        // expression
        compileExpression(level);
        // ')'
        printSymbol(level);
        // '{'
        printSymbol(level);
        // statements
        compileStatements();
        // '}'
        printSymbol(level);

        out.println(indent(level-1) + "</ifStatement>");
    }

    public void CompileExpression() {

    }

    public void CompileTerm() {

    }

    public void CompileExpressionList() {

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
        if (level == 2)
            indent += indent; // double the indentation level
        else if (level == 3)
            indent += indent + indent;
        else if (level == 4)
            indent += indent + indent + indent;
        else if (level == 5)
            indent += indent + indent + indent + indent;
        else if (level == 6)
            indent += indent + indent + indent + indent + indent;

        return indent;
    }

    private void printKeyword(int level) {
        if (jt.tokenType() == TokenType.KEYWORD) {
            out.println(indent(level) + "<keyword> " + jt.keyWord() + " </keyword>");
            jt.advance();
        } else {
            // error out: keyword expected
        }
    }

    private void printSymbol(int level) {
        if (jt.tokenType() == TokenType.SYMBOL) {
            out.println(indent(level) + "<symbol> " + jt.symbol() + " </symbol>");
            jt.advance();
        } else {
            // wrong structure; error out: symbol expected
        }
    }

    private void printIdentifier(int level) {
        if (jt.tokenType() == TokenType.IDENTIFIER) {
            out.println(indent(level) + "<identifier>" + jt.identifier() + " </identifier>");
            jt.advance();
        } else {
            // wrong structure, error out: identifier expected
        }
    }
}
