import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
//    private HashMap<String, HashMap<String, HashMap<Kind, Integer>>> classScope;
//    private HashMap<String, Integer> classScope;
    // private HashMap<Kind, Integer> subIndex;
    // private int[][][] properties;
//    private HashMap<Kind, Integer> varCounts;
//    private HashMap<Integer, String> varTypes;
    //private HashMap<String, HashMap<Kind, Integer>> subType;
//    private HashMap<String, HashMap<String, HashMap<Kind, Integer>>> subroutineScope;
//    private HashMap<String, Integer> subroutineScope;
    // private HashMap<String, Integer> subroutineScope;
//    private HashMap<String, HashMap<String, Kind>> classScope;
//    private HashMap<String, HashMap<String, Kind>> subroutineScope;


    private HashMap<String, Integer> subIndexes;
    private HashMap<String, String> subTypes;
//    private Map<String, HashMap<String, HashMap<Kind, Integer>>> classScope;
//    private Map<String, HashMap<String, HashMap<Kind, Integer>>> subroutineScope;
    private HashMap<String, Kind> classScope;
    private HashMap<String, Kind> subroutineScope;
    private int[] varCounts;

    public SymbolTable() {
        this.classScope = new HashMap<>();
        // this.subroutineScope = new HashMap<String, Map<String, Map<Kind, Integer>>>();
        startSubroutine();
    }
    /* Starts a new subroutine scope (i.e. resets the subroutine's symbol table */
    public void startSubroutine() {
        this.subroutineScope = new HashMap<>();
        this.varCounts = new int[4]; // STATIC, FIELD, ARG, VAR
//        this.varTypes = new HashMap<>();
    }

    public void define(String name, String type, Kind kind) {
        subIndexes = new HashMap<>();
//        Map<String, Map<Kind, Integer>> subType = new HashMap<>();
        this.varCounts[varCount(kind)]++;
        subIndexes.put(name, varCounts[varCount(kind)]);
        subTypes.put(name, type);
//        HashMap<String, HashMap<Kind, Integer>> temp = new HashMap<>();
//        temp.put(type, subIndex);
        if (kind == Kind.STATIC || kind == Kind.FIELD) {
            // subroutine Scope
            this.classScope.put(name, kind);

        } else if (kind == Kind.ARG || kind == Kind.VAR){
            // class Scope
            this.subroutineScope.put(name, kind);
        }
        // increase counts
    }

    public int varCount(Kind kind) {
        Kind[] indexes = { Kind.STATIC, Kind.FIELD, Kind.ARG, Kind.VAR };
        return varCounts[Arrays.asList(indexes).indexOf(kind)];
    }

    public Kind kindOf(String name) {
//        HashMap<String, Kind> temp = this.subroutineScope.get(name);
        return this.subroutineScope.get(name);
    }

    public String typeOf(String name) {
        return this.subTypes.get(name);
    }

    public int indexOf(String name) {
        return this.subIndexes.get(name);
    }
}
enum Kind {
    STATIC,
    FIELD,
    ARG,
    VAR,
    NONE
}
