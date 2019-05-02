import java.io.File;
import java.io.PrintWriter;

// for pointerBack, I could probably use if-else
public class CompilationEngine {

    private VMWriter vmWriter;
    private JackTokenizer tokenizer;
    private SymbolTable symbolTable;
    private String currentClass;
    private String currentSubroutine;
    private int labelIndex;

    // public CompilationEngine(File inFile, File outFile) {
    public CompilationEngine(PrintWriter p, JackTokenizer jt) {

        // tokenizer = new JackTokenizer(inFile);
        this.tokenizer = jt;
        vmWriter = new VMWriter(p);
        this.symbolTable = new SymbolTable();

        labelIndex = 0;
        compileClass();

    }

    private String currentFunction() {

        if (currentClass.length() != 0 && currentSubroutine.length() !=0) {

            return currentClass + "." + currentSubroutine;

        }

        return "";
    }

    private String compileType() {

        tokenizer.advance();

        if (tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().matches("int|char|boolean|className"))) {
            return tokenizer.keyWord();
        }

        if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
            return tokenizer.identifier();
        }

        error("int|char|boolean|className");

        return "";
    }

    public void compileClass() {

        //'class'
        tokenizer.advance();

        if (tokenizer.tokenType() != TokenType.KEYWORD || !tokenizer.keyWord().equals("class")) {
            error("class");
        }

        //className
        tokenizer.advance();

        if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
            error("className");
        }

        //classname does not need to be put in symbol table
        currentClass = tokenizer.identifier();

        //'{'
        requireSymbol('{');

        //classVarDec* subroutineDec*
        compileClassVarDec();
        compileSubroutine();

        //'}'
        requireSymbol('}');

        if (tokenizer.hasMoreTokens()) {
            throw new IllegalStateException("Unexpected tokens");
        }

        //save file
        vmWriter.close();
    }

    /**
     * Compiles a static declaration or a field declaration
     * classVarDec ('static'|'field') type varName (','varNAme)* ';'
     */
    private void compileClassVarDec() {

        //first determine whether there is a classVarDec, nextToken is } or start subroutineDec
        tokenizer.advance();

        //next is a '}'
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}') {
            tokenizer.pointerBack();
//            error("Symbol");
            return;
        }

        //next is start subroutineDec or classVarDec, both start with keyword
        if (tokenizer.tokenType() != TokenType.KEYWORD) {
            error("Keywords");
        }

        //next is subroutineDec
        if (tokenizer.keyWord().matches("constructor|function|method")) {
            tokenizer.pointerBack();
            // error("constructor|function|method");
            return;
        }

        //classVarDec exists
//        if (tokenizer.keyWord() != JackTokenizer.KEYWORD.STATIC && tokenizer.keyWord() != JackTokenizer.KEYWORD.FIELD) {
        if (!tokenizer.keyWord().equals("static") && !tokenizer.keyWord().equals("field")) {
            error("static or field");
        }

        Kind kind = null;
        String type = "";
        String name = "";

        switch (tokenizer.keyWord()) {
            case "static":
                kind = Kind.STATIC;
                break;
            case "field":
                kind = Kind.FIELD;
                break;
        }

        //type
        type = compileType();

        //at least one varName
        boolean varNamesDone = false;

        do {

            //varName
            tokenizer.advance();
            if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
                error("identifier");
            }

            name = tokenizer.identifier();

            symbolTable.define(name, type, kind);

            //',' or ';'
            tokenizer.advance();

            if (tokenizer.tokenType() != TokenType.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')) {
                error("',' or ';'");
            }

            if (tokenizer.symbol() == ';') {
                break;
            }


        }while(true);

        compileClassVarDec();
    }

    /** Compiles a complete method function or constructor */
    private void compileSubroutine() {

        //determine whether there is a subroutine, next can be a '}'
        tokenizer.advance();

        //next is a '}'
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}') {
            tokenizer.pointerBack();
//            error("Symbol");
            return;
        }

        //start of a subroutine
//        if (tokenizer.tokenType() != TokenType.KEYWORD || (tokenizer.keyWord() != JackTokenizer.KEYWORD.CONSTRUCTOR && tokenizer.keyWord() != JackTokenizer.KEYWORD.FUNCTION && tokenizer.keyWord() != JackTokenizer.KEYWORD.METHOD)) {
        if (tokenizer.tokenType() != TokenType.KEYWORD || (!tokenizer.keyWord().matches("constructor|function|method"))) {
            error("constructor|function|method");
        }

//        JackTokenizer.KEYWORD keyword = tokenizer.keyWord();
        String keyword = tokenizer.keyWord();

        symbolTable.startSubroutine();

        //for method this is the first argument
//        if (tokenizer.keyWord() == JackTokenizer.KEYWORD.METHOD) {
        if (tokenizer.keyWord().matches("method")) {
            symbolTable.define("this",currentClass, Kind.ARG);
        }

        String type = "";

        //'void' or type
        tokenizer.advance();
//        if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord() == JackTokenizer.KEYWORD.VOID) {
        if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("void")) {
            type = "void";
        }else {
            tokenizer.pointerBack();
            type = compileType();
        }

        //subroutineName which is a identifier
        tokenizer.advance();
        if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
            error("subroutineName");
        }

        currentSubroutine = tokenizer.identifier();

        //'('
        requireSymbol('(');

        //parameterList
        compileParameterList();

        //')'
        requireSymbol(')');

        //subroutineBody
        compileSubroutineBody(keyword);

        compileSubroutine();

    }

    /**
     * Compiles the body of a subroutine
     * '{'  varDec* statements '}'
     */
//    private void compileSubroutineBody(JackTokenizer.KEYWORD keyword) {
    private void compileSubroutineBody(String keyword) {
        //'{'
        requireSymbol('{');
        //varDec*
        compileVarDec();
        //write VM function declaration
        wrtieFunctionDec(keyword);
        //statements
        compileStatement();
        //'}'
        requireSymbol('}');
    }

    /**
     * write function declaration, load pointer when keyword is METHOD or CONSTRUCTOR
     */
    private void wrtieFunctionDec(String keyword) {

        vmWriter.writeFunction(currentFunction(),symbolTable.varCount(Kind.VAR));

        //METHOD and CONSTRUCTOR need to load this pointer
        if (keyword.equals("method")) {
            //A Jack method with k arguments is compiled into a VM function that operates on k + 1 arguments.
            // The first argument (argument number 0) always refers to the this object.
            vmWriter.writePush(Segment.ARG, 0);
            vmWriter.writePop(Segment.POINTER,0);

        }else if (keyword.equals("constructor")) {
            //A Jack function or constructor with k arguments is compiled into a VM function that operates on k arguments.
            vmWriter.writePush(Segment.CONST,symbolTable.varCount(Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(Segment.POINTER,0);
        }
    }

    private void compileStatement() {

        //determine whether there is a statement next can be a '}'
        tokenizer.advance();

        //next is a '}'
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '}') {
            tokenizer.pointerBack();
            return;
        }

        //next is 'let'|'if'|'while'|'do'|'return'
        if (tokenizer.tokenType() != TokenType.KEYWORD) {
            error("keyword");
        }else {
            switch (tokenizer.keyWord()) {
                case "let":
                    compileLet();
                    break;
                case "if":
                    compileIf();
                    break;
                case "while":
                    compilesWhile();
                    break;
                case "do":
                    compileDo();
                    break;
                case "return":
                    compileReturn();
                    break;
                default:
                    error("'let'|'if'|'while'|'do'|'return'");
            }
        }

        compileStatement();
    }

    /**
     * Compiles a (possibly empty) parameter list
     * not including the enclosing "()"
     * ((type varName)(',' type varName)*)?
     */
    private void compileParameterList() {

        //check if there is parameterList, if next token is ')' than go back
        tokenizer.advance();
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')') {
            tokenizer.pointerBack();
            return;
        }

        String type = "";

        //there is parameter, at least one varName
        tokenizer.pointerBack();
        do {
            //type
            type = compileType();

            //varName
            tokenizer.advance();
            if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
                error("identifier");
            }

            symbolTable.define(tokenizer.identifier(), type, Kind.ARG);

            //',' or ')'
            tokenizer.advance();
            if (tokenizer.tokenType() != TokenType.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ')')) {
                error("',' or ')'");
            }

            if (tokenizer.symbol() == ')') {
                tokenizer.pointerBack();
                break;
            }

        } while(true);

    }

    /**
     * Compiles a var declaration
     * 'var' type varName (',' varName)*;
     */
    private void compileVarDec() {

        //determine if there is a varDec

        tokenizer.advance();
        //no 'var' go back
        if (tokenizer.tokenType() != TokenType.KEYWORD || !tokenizer.keyWord().equals("var")) {
            tokenizer.pointerBack();
            return;
        }

        //type
        String type = compileType();

        //at least one varName
        boolean varNamesDone = false;

        do {

            //varName
            tokenizer.advance();

            if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
                error("identifier");
            }

            symbolTable.define(tokenizer.identifier(), type, Kind.VAR);

            //',' or ';'
            tokenizer.advance();

            if (tokenizer.tokenType() != TokenType.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')) {
                error("',' or ';'");
            }

            if (tokenizer.symbol() == ';') {
                break;
            }


        }while(true);

        compileVarDec();

    }

    /**
     * Compiles a do statement
     * 'do' subroutineCall ';'
     */
    private void compileDo() {

        //subroutineCall
        compileSubroutineCall();
        //';'
        requireSymbol(';');
        //pop return value
        vmWriter.writePop(Segment.TEMP,0);
    }

    /**
     * Compiles a let statement
     * 'let' varName ('[' ']')? '=' expression ';'
     */
    private void compileLet() {

        //varName
        tokenizer.advance();
        if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
            error("varName");
        }

        String varName = tokenizer.identifier();

        //'[' or '='
        tokenizer.advance();
        if (tokenizer.tokenType() != TokenType.SYMBOL || (tokenizer.symbol() != '[' && tokenizer.symbol() != '=')) {
            error("'['|'='");
        }

        boolean expExist = false;

        //'[' expression ']' ,need to deal with array [base+offset]
        if (tokenizer.symbol() == '[') {

            expExist = true;

            //push array variable,base address into stack
            vmWriter.writePush(getSeg(symbolTable.kindOf(varName)),symbolTable.indexOf(varName));

            //calc offset
            compileExpression();

            //']'
            requireSymbol(']');

            //base+offset
            vmWriter.writeArithmetic(Command.ADD);
        }

        if (expExist) tokenizer.advance();

        //expression
        compileExpression();

        //';'
        requireSymbol(';');

        if (expExist) {
            //*(base+offset) = expression
            //pop expression value to temp
            vmWriter.writePop(Segment.TEMP,0);
            //pop base+index into 'that'
            vmWriter.writePop(Segment.POINTER,1);
            //pop expression value into *(base+index)
            vmWriter.writePush(Segment.TEMP,0);
            vmWriter.writePop(Segment.THAT,0);
        }else {
            //pop expression value directly
            vmWriter.writePop(getSeg(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));

        }
    }

    /**
     * return corresponding seg for input kind
     * @param kind
     * @return
     */
    private Segment getSeg(Kind kind) {

        switch (kind) {
            case FIELD:
                return Segment.THIS;
            case STATIC:
                return Segment.STATIC;
            case VAR:
                return Segment.LOCAL;
            case ARG:
                return Segment.ARG;
            default:
                return Segment.NONE;
        }

    }

    /**
     * Compiles a while statement
     * 'while' '(' expression ')' '{' statements '}'
     */
    private void compilesWhile() {

        String continueLabel = newLabel();
        String topLabel = newLabel();

        //top label for while loop
        vmWriter.writeLabel(topLabel);

        //'('
        requireSymbol('(');
        //expression while condition: true or false
        compileExpression();
        //')'
        requireSymbol(')');
        //if ~(condition) go to continue label
        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf(continueLabel);
        //'{'
        requireSymbol('{');
        //statements
        compileStatement();
        //'}'
        requireSymbol('}');
        //if (condition) go to top label
        vmWriter.writeGoto(topLabel);
        //or continue
        vmWriter.writeLabel(continueLabel);
    }

    private String newLabel() {
        return "LABEL_" + (labelIndex++);
    }

    /**
     * Compiles a return statement
     * ‘return’ expression? ';'
     */
    private void compileReturn() {

        //check if there is any expression
        tokenizer.advance();

        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ';') {
            //no expression push 0 to stack
            vmWriter.writePush(Segment.CONST,0);
        }else {
            //expression exist
            tokenizer.pointerBack();
            //expression
            compileExpression();
            //';'
            requireSymbol(';');
        }

        vmWriter.writeReturn();

    }

    /**
     * Compiles an if statement
     * possibly with a trailing else clause
     * 'if' '(' expression ')' '{' statements '}' ('else' '{' statements '}')?
     */
    private void compileIf() {

        String elseLabel = newLabel();
        String endLabel = newLabel();

        //'('
        requireSymbol('(');
        //expression
        compileExpression();
        //')'
        requireSymbol(')');
        //if ~(condition) go to else label
        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf(elseLabel);
        //'{'
        requireSymbol('{');
        //statements
        compileStatement();
        //'}'
        requireSymbol('}');
        //if condition after statement finishing, go to end label
        vmWriter.writeGoto(endLabel);

        vmWriter.writeLabel(elseLabel);
        //check if there is 'else'
        tokenizer.advance();
        if (tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("else")) {
            //'{'
            requireSymbol('{');
            //statements
            compileStatement();
            //'}'
            requireSymbol('}');
        }else {
            tokenizer.pointerBack();
        }

        vmWriter.writeLabel(endLabel);

    }

    /**
     * Compiles a term.
     * This routine is faced with a slight difficulty when trying to decide between some of the alternative parsing rules.
     * Specifically, if the current token is an identifier
     *      the routine must distinguish between a variable, an array entry and a subroutine call
     * A single look-ahead token, which may be one of "[" "(" "." suffices to distinguish between the three possibilities
     * Any other token is not part of this term and should not be advanced over
     *
     * integerConstant|stringConstant|keywordConstant|varName|varName '[' expression ']'|subroutineCall|
     * '(' expression ')'|unaryOp term
     */
    private void compileTerm() {

        tokenizer.advance();
        //check if it is an identifier
        if (tokenizer.tokenType() == TokenType.IDENTIFIER) {
            //varName|varName '[' expression ']'|subroutineCall
            String tempId = tokenizer.identifier();

            tokenizer.advance();
            if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '[') {
                //this is an array entry

                //push array variable,base address into stack
                vmWriter.writePush(getSeg(symbolTable.kindOf(tempId)),symbolTable.indexOf(tempId));

                //expression
                compileExpression();
                //']'
                requireSymbol(']');

                //base+offset
                vmWriter.writeArithmetic(Command.ADD);

                //pop into 'that' pointer
                vmWriter.writePop(Segment.POINTER,1);
                //push *(base+index) onto stack
                vmWriter.writePush(Segment.THAT,0);

            }else if (tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')) {
                //this is a subroutineCall
                tokenizer.pointerBack();
//                tokenizer.pointerBack(); call pointerPrevious instead
                tokenizer.pointerPrevious();
                compileSubroutineCall();
            }else {
                //this is varName
                tokenizer.pointerBack();
                //push variable directly onto stack
                vmWriter.writePush(getSeg(symbolTable.kindOf(tempId)), symbolTable.indexOf(tempId));
            }

        }else{
            //integerConstant|stringConstant|keywordConstant|'(' expression ')'|unaryOp term
            if (tokenizer.tokenType() == TokenType.INT_CONST) {
                //integerConstant just push its value onto stack
                vmWriter.writePush(Segment.CONST,tokenizer.intVal());
            }else if (tokenizer.tokenType() == TokenType.STRING_CONST) {
                //stringConstant new a string and append every char to the new stack
                String str = tokenizer.stringVal();

                vmWriter.writePush(Segment.CONST,str.length());
                vmWriter.writeCall("String.new",1);

                for (int i = 0; i < str.length(); i++) {
                    vmWriter.writePush(Segment.CONST,(int)str.charAt(i));
                    vmWriter.writeCall("String.appendChar",2);
                }

            }else if(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("true")) {
                //~0 is true
                vmWriter.writePush(Segment.CONST,0);
                vmWriter.writeArithmetic(Command.NOT);

            }else if(tokenizer.tokenType() == TokenType.KEYWORD && tokenizer.keyWord().equals("this")) {
                //push this pointer onto stack
                vmWriter.writePush(Segment.POINTER,0);

            }else if(tokenizer.tokenType() == TokenType.KEYWORD && (tokenizer.keyWord().matches("false|null"))) {
                //0 for false and null
                vmWriter.writePush(Segment.CONST,0);
            }else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '(') {
                //expression
                compileExpression();
                //')'
                requireSymbol(')');
            }else if (tokenizer.tokenType() == TokenType.SYMBOL && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')) {

                char s = tokenizer.symbol();

                //term
                compileTerm();

                if (s == '-') {
                    vmWriter.writeArithmetic(Command.NEG);
                }else {
                    vmWriter.writeArithmetic(Command.NOT);
                }

            }else {
                error("integerConstant|stringConstant|keywordConstant|'(' expression ')'|unaryOp term");
            }
        }

    }

    /**
     * Compiles a subroutine call
     * subroutineName '(' expressionList ')' | (className|varName) '.' subroutineName '(' expressionList ')'
     */
    private void compileSubroutineCall() {

        tokenizer.advance();
        if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
            error("identifier");
        }

        String name = tokenizer.identifier();
        int nArgs = 0;

        tokenizer.advance();
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '(') {
            //push this pointer
            vmWriter.writePush(Segment.POINTER,0);
            //'(' expressionList ')'
            //expressionList
            nArgs = compileExpressionList() + 1;
            //')'
            requireSymbol(')');
            //call subroutine
            vmWriter.writeCall(currentClass + '.' + name, nArgs);

        }else if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == '.') {
            //(className|varName) '.' subroutineName '(' expressionList ')'

            String objName = name;
            //subroutineName
            tokenizer.advance();
            if (tokenizer.tokenType() != TokenType.IDENTIFIER) {
                error("identifier");
            }

            name = tokenizer.identifier();

            //check for if it is built-in type
            String type = symbolTable.typeOf(objName);

            if (type.equals("int")||type.equals("boolean")||type.equals("char")||type.equals("void")) {
                error("no built-in type");
            }else if (type.equals("")) {
                name = objName + "." + name;
            }else {
                nArgs = 1;
                //push variable directly onto stack
                vmWriter.writePush(getSeg(symbolTable.kindOf(objName)), symbolTable.indexOf(objName));
                name = symbolTable.typeOf(objName) + "." + name;
            }

            //'('
            requireSymbol('(');
            //expressionList
            nArgs += compileExpressionList();
            //')'
            requireSymbol(')');
            //call subroutine
            vmWriter.writeCall(name,nArgs);
        }else {
            error("'('|'.'");
        }

    }

    /**
     * Compiles an expression
     * term (op term)*
     */
    private void compileExpression() {
        //term
        compileTerm();
        //(op term)*
        do {
            tokenizer.advance();
            //op
            if (tokenizer.tokenType() == TokenType.SYMBOL && isOperator(tokenizer.symbol())) {

                Command opCmd = Command.NONE;

                switch (tokenizer.symbol()) {
                    case '+':
                        opCmd = Command.ADD;
                        break;
                    case '-':
                        opCmd = Command.SUB;
                        break;
                    case '*':
                        opCmd = Command.MULT;
                        break;
                    case '/':
                        opCmd = Command.DIV;
                        break;
                    case '<':
                        opCmd = Command.LT;
                        break;
                    case '>':
                        opCmd = Command.GT;
                        break;
                    case '=':
                        opCmd = Command.EQ;
                        break;
                    case '&':
                        opCmd = Command.AND;
                        break;
                    case '|':
                        opCmd = Command.OR;
                        break;
                    default:
                        error("Unknown op!");
                }

                //term
                compileTerm();

//                vmWriter.writeCommand(opCmd,"","");
                vmWriter.writeArithmetic(opCmd);

            }else {
                tokenizer.pointerBack();
                break;
            }

        }while (true);

    }

    /**
     * Compiles a (possibly empty) comma-separated list of expressions
     * (expression(','expression)*)?
     * @return nArgs
     */
    private int compileExpressionList() {
        int nArgs = 0;

        tokenizer.advance();
        //determine if there is any expression, if next is ')' then no
        if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ')') {
             tokenizer.pointerBack();
        }else {
            nArgs = 1;
            tokenizer.pointerBack();
            //expression
            compileExpression();
            //(','expression)*
            do {
                tokenizer.advance();
                if (tokenizer.tokenType() == TokenType.SYMBOL && tokenizer.symbol() == ',') {
                    //expression
                    compileExpression();
                    nArgs++;
                }else {
                    tokenizer.pointerBack();
                    break;
                }

            }while (true);
        }

        return nArgs;
    }

    /**
     * throw an exception to report errors
     * @param val
     */
    private void error(String val) {
        throw new IllegalStateException("Expected token missing : " + val + " Current token:" + tokenizer.stringVal());
    }

    /**
     * require symbol when we know there must be such symbol
     * @param symbol
     */
    private void requireSymbol(char symbol) {
        tokenizer.advance();
        if (tokenizer.tokenType() != TokenType.SYMBOL || tokenizer.symbol() != symbol) {
            error("'" + symbol + "'");
        }
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
}
