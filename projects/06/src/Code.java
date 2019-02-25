import java.util.Hashtable;

class Code {
    public String dest(String dest) {
        // Returns the "dest" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "";
        // first digit
        if (dest.contains("A"))
            ret += "1";
        else
            ret += "0";
        // second digit
        if (dest.contains("D"))
            ret += "1";
        else
            ret += "0";
        // third digit
        if (dest.contains("M"))
            ret += "1";
        else
            ret += "0";
                
        return ret;
    }

    public String comp(String comp) {
        Hashtable<String, String> comps = new Hashtable<>();
        comps.put("0",   "0101010");
        comps.put("1",   "0111111");
        comps.put("-1",  "0111010");
        comps.put("D",   "0001100");
        comps.put("A",   "0110000");
        comps.put("!D",  "0001101");
        comps.put("!A",  "0110001");
        comps.put("-D",  "0001111");
        comps.put("-A",  "0110011");
        comps.put("D+1", "0011111");
        comps.put("A+1", "0110111");
        comps.put("D-1", "0001110");
        comps.put("A-1", "0110010");
        comps.put("D+A", "0000010");
        comps.put("D-A", "0010011");
        comps.put("A-D", "0000111");
        comps.put("D&A", "0000000");
        comps.put("D|A", "0010101");

        comps.put("M",   "1110000");
        comps.put("!M",  "1110001");
        comps.put("-M",  "1110011");
        comps.put("M+1", "1110111");
        comps.put("M-1", "1110010");
        comps.put("D+M", "1000010");
        comps.put("D-M", "1010011");
        comps.put("M-D", "1000111");
        comps.put("D&M", "1000000");
        comps.put("D|M", "1010101");

        return comps.get(comp);
    }

    public String jump(String jump) {
        // Returns the "jump" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "";
        if (jump.equals("JGT"))
            ret = "001";
        else if (jump.equals("JEQ"))
            ret = "010";
        else if (jump.equals("JGE"))
            ret = "011";
        else if (jump.equals("JLT"))
            ret = "100";
        else if (jump.equals("JNE"))
            ret = "101";
        else if (jump.equals("JLE"))
            ret = "110";
        else if (jump.equals("JMP"))
            ret = "111";
        else
            ret = "000";

        return ret;
    }
}