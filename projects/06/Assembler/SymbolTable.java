// package Assemble;

import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Integer> symbols = new HashMap<>();

    public SymbolTable() {
        addEntry("SP", 0);
        addEntry("R0", 0);
        addEntry("LCL", 1);
        addEntry("R1", 1);
        addEntry("ARG", 2);
        addEntry("R2", 2);
        addEntry("THIS", 3);
        addEntry("R3", 3);
        addEntry("THAT", 4);
        addEntry("R4", 4);
        addEntry("R5", 5);
        addEntry("R6", 6);
        addEntry("R7", 7);
        addEntry("R8", 8);
        addEntry("R9", 9);
        addEntry("R10", 10);
        addEntry("R11", 11);
        addEntry("R12", 12);
        addEntry("R13", 13);
        addEntry("R14", 14);
        addEntry("R15", 15);
        addEntry("SCREEN", 16384);
        addEntry("KBD", 24576);
    }
    public void addEntry(String symbol, int address) {
        symbols.put(symbol, address);
    }
    public Boolean contains(String symbol) {
        return symbols.containsKey(symbol);
    }
    public int getAddress(String symbol) {
        return symbols.get(symbol);
    }
}
