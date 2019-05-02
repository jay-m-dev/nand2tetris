import java.util.HashMap;

public class SymbolTable {
    private HashMap<String,Symbol> classSymbols;//for STATIC, FIELD
    private HashMap<String,Symbol> subroutineSymbols;//for ARG, VAR
    private HashMap<Kind,Integer> indices;

    public SymbolTable() {
        classSymbols = new HashMap<>();
        subroutineSymbols = new HashMap<>();

        indices = new HashMap<>();
        indices.put(Kind.ARG,0);
        indices.put(Kind.FIELD,0);
        indices.put(Kind.STATIC,0);
        indices.put(Kind.VAR,0);

    }

    public void startSubroutine() {
        subroutineSymbols.clear();
        indices.put(Kind.VAR,0);
        indices.put(Kind.ARG,0);
    }

    public void define(String name, String type, Kind kind) {

        if (kind == Kind.ARG || kind == Kind.VAR) {

            int index = indices.get(kind);
            Symbol symbol = new Symbol(type,kind,index);
            indices.put(kind,index+1);
            subroutineSymbols.put(name,symbol);

        } else if(kind == Kind.STATIC || kind == Kind.FIELD) {

            int index = indices.get(kind);
            Symbol symbol = new Symbol(type,kind,index);
            indices.put(kind,index+1);
            classSymbols.put(name,symbol);

        }

    }

    public int varCount(Kind kind){
        return indices.get(kind);
    }

    public Kind kindOf(String name) {

        Symbol symbol = lookUp(name);

        if (symbol != null) return symbol.getKind();

        return Kind.NONE;
    }

    public String typeOf(String name) {

        Symbol symbol = lookUp(name);

        if (symbol != null) return symbol.getType();

        return "";
    }

    public int indexOf(String name) {

        Symbol symbol = lookUp(name);

        if (symbol != null) return symbol.getIndex();

        return -1;
    }

    private Symbol lookUp(String name) {

        if (classSymbols.get(name) != null) {
            return classSymbols.get(name);
        } else if (subroutineSymbols.get(name) != null) {
            return subroutineSymbols.get(name);
        } else {
            return null;
        }

    }
}
