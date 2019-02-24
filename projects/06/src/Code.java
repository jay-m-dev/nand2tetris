class Code {
    private String dest(String dest) {
        // Returns the "dest" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "";
        // first digit
        if (dest.contains("A"))
            ret += "1";
        else
            ret += "0";
        // second digit
        if (dest.contains("M"))
            ret += "1";
        else
            ret += "0";
        // third digit
        if (dest.contains("D"))
            ret += "1";
        else
            ret += "0";
                
        return ret;
    }

    private String comp(String compMnem) {

    }

    private String jump(String jump) {
        // Returns the "jump" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "";
        if jump.equals("JGT")
            ret = "001";
        else if jump.equals("JEQ")
            ret = "010";
        else if jump.equals("JGE")
            ret = "011";
        else if jump.equals("JLT")
            ret = "100";
        else if jump.equals("JNE")
            ret = "101";
        else if jump.equals("JLE")
            ret = "110";
        else if jump.equals("JMP")
            ret = "111";
        else
            ret = "000";

        return ret;
    }
}