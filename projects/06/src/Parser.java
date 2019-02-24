import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
class Parser {
    private BufferedReader inputStream;
    private String currentCommand;
    // constructor
    public Parser(String fileName) {
        // opens the filestream and gets ready to parse it.
        try {
            inputStream = new BufferedReader( new FileReader( fileName ) );
        } catch (FileNotFoundException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
    private Boolean hasMoreCommands() {
        // determines if there are more commands in the input
        // the line should not be an empty line or a comment
        String line = null;

        try {
            while ((line = inputStream.readLine()) != null) {
                line = line.trim(); // remove white space
                if (!(line.startsWith("//") || line.isEmpty()))
                    return true;
            } 
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

        return false;
    }
    private void advance() {
        // reads the next command from the input and makes it the current command
        // Should be called only if hasMoreCommands returns true
        String line = null;

        try {
            while ((line = inputStream.readLine()) != null) {
                line = line.trim(); // remove white space
                if (!(line.startsWith("//") || line.isEmpty())) {
                    this.currentCommand = line;
                    break;
                }
            } 
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
    enum Command {
        A_COMMAND,
        C_COMMAND,
        L_COMMAND
    }
    private Command commandType() {
        if (this.currentCommand.startsWith("@") || isNumeric(this.currentCommand))
            return Command.A_COMMAND;
        else if (this.currentCommand.matches(".*[=;].*"))
            return Command.C_COMMAND;
        else
            return Command.L_COMMAND;
    }
    private Boolean isNumeric(String str)  
    {  
        try  
        {  
            double d = Double.parseDouble(str);  
        }  
        catch(NumberFormatException e)  {  
            return false;  
        }  
        return true;  
    }
    private String symbol() {
        // returns the symbol or decimal Xxx of the current command @Xxx or (Xxx)
        // Should be called only when commandType() is A_COMMAND or L_COMMAND
        int i = Integer.parseInt(this.currentCommand);
        return Integer.toBinaryString(i);
    }
    private String dest() {
        // Returns the "dest" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "null";
        if (this.currentCommand.contains("="))
            ret = this.currentCommand.split("=")[0];

        return ret;
    }
    private String comp() {
        // Returns the "comp" mnemonic in the current C-command (28 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = this.currentCommand;
        if (ret.contains("="))
            ret = ret.split("=")[1]; // get the second token
        
        // let's check if comp has a ; sign, strip it if it does
        if (ret.contains(";"))
            ret = ret.split(";")[0]; // we want the first token
        return ret;
    }
    private String jump() {
        // Returns the "jump" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "null";

        if (this.currentCommand.contains(";"));
            ret = this.currentCommand.split(";")[1];

        return ret;
    }
}