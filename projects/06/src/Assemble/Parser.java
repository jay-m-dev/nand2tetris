package Assemble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Queue;

import java.util.LinkedList;

class Parser {
    private BufferedReader inputStream;
    private String currentCommand;
    private Queue<String> queue = new LinkedList<String>();
    // constructor
    public Parser(String fileName) {
        // opens the filestream and gets ready to parse it.
        String line = null;
        try {
            inputStream = new BufferedReader( new FileReader( fileName ) );
            while ((line = inputStream.readLine()) != null) {
                line = line.trim();
                if (!(line.startsWith("//") || line.isEmpty())) {
                    queue.add(line);
                }
            }
        // } catch (FileNotFoundException | IOException e) {
        } catch (IOException e) {
            System.err.format("Exception: %s%n", e);
        }
    }
    public String getCurrentCommand() {
        return this.currentCommand;
    }
    public Boolean hasMoreCommands() {
        // determines if there are more commands in the input
        // the line should not be an empty line or a comment
        return (queue.peek() != null);
    }
    public void advance() {
        // reads the next command from the input and makes it the current command
        // Should be called only if hasMoreCommands returns true
        this.currentCommand = queue.poll();
    }
   
    public Command commandType() {
        if (this.currentCommand.startsWith("@")){// || isNumeric(this.currentCommand))

            return Command.A_COMMAND;
        }
        else if (this.currentCommand.matches(".*[=;].*"))
            return Command.C_COMMAND;
        else
            return Command.L_COMMAND;
    }
    private Boolean isNumeric(String str)  {  
        try  {  
            double d = Double.parseDouble(str);  
        }  
        catch(NumberFormatException e)  {  
            return false;  
        }  
        return true;  
    }
    public String symbol() {
        // returns the symbol or decimal Xxx of the current command @Xxx or (Xxx)
        // Should be called only when commandType() is A_COMMAND or L_COMMAND
        if (this.currentCommand.startsWith("@"))
            this.currentCommand = this.currentCommand.substring(1);
        int i = Integer.parseInt(this.currentCommand);
        return String.format("%15s", Integer.toBinaryString(i)).replace(' ', '0'); // pad with '0' on the left
    }
    public String dest() {
        // Returns the "dest" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "null";
        if (this.currentCommand.contains("="))
            ret = this.currentCommand.split("=")[0];

        return ret;
    }
    public String comp() {
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
    public String jump() {
        // Returns the "jump" mnemonic in the current C-command (8 possibilities)
        // Should be called only when commandType() is C_COMMAND
        String ret = "null";

        if (this.currentCommand.contains(";"))
            ret = this.currentCommand.split(";")[1];
            // System.out.println(this.currentCommand.split(";")[0]);
            // System.out.println(this.currentCommand.split(";")[1]);

        return ret;
    }
}
enum Command {
    A_COMMAND,
    C_COMMAND,
    L_COMMAND
}