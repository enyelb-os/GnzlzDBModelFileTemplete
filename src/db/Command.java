package db;

import java.util.ArrayList;

public class Command {

    /***************************************
     * Default list Commands
     ***************************************/

    private static ArrayList<Command> listCommands = new ArrayList<Command>();
    public static ListCommand listCommand = new ListCommand(listCommands);
    static {
        Command.command("host"      , ""        , "--host"      , "-h");
        Command.command("port"      , -1        , "--port");
        Command.command("user"      , "root"    , "--user"      , "-u");
        Command.command("password"  , ""        , "--pass"      , "-p");
        Command.command("name"      , ""        , "--name"      , "-n");
        Command.command("type"      , "mysql"   , "--type"      , "-t");
        Command.command("modules"   , false     , "--modules"   , "-m");
    }

    /***************************************
     * vars
     ***************************************/

    final ArrayList<String> commands;
    final String name;
    Object value;

    /***************************************
     * constructor
     ***************************************/

    public Command(String name, Object value, String ... commands){
        this.commands = new ArrayList<String>();
        this.name = name;
        this.addCommand(commands);
        this.value = value;
    }
    public static void command(String name, Object value, String ... commands){
        boolean newCommand = true;
        for (Command command: listCommands) {
            if(command.name.equals(name)){
                newCommand = false;
                command.value = value;
                command.addCommand(commands);
                break;
            }
        }
        if(newCommand){
            listCommands.add(new Command(name,value, commands));
        }
    }

    private void addCommand(String ... commands){
        for (String commandName : commands) {
            boolean exist = false;
            for (String commandNameOld: this.commands) {
                if (commandNameOld.equals(commandName)) {
                    exist = true;
                }
            }
            if(!exist){
                this.commands.add(commandName);
            }
        }

        for (Command command: listCommands) {
            if(!command.name.equals(name)) {
                for (String commandName : commands) {
                    for (String commandNameOld: command.commands) {
                        if (commandNameOld.equals(commandName)) {
                            throw new RuntimeException("command duplicate : " + command.name + "." + commandName + " == " + name + "." + commandName);
                        }
                    }
                }
            }
        }
    }

    public static ListCommand process(String[] args){
        String option = "";
        for (String code: args) {
            for (Command command: listCommands) {
                if (code.substring(0, 1).equals("-")) {
                    option = code;
                    for (String commandOption: command.commands) {
                        if (option.equals(commandOption) && command.value instanceof Boolean) {
                            command.value = true;
                            break;
                        }
                    }
                } else {
                    for (String commandOption: command.commands) {
                        if (option.equals(commandOption)) {
                            if(command.value instanceof Integer){
                                command.value = Integer.parseInt(code);
                            } else {
                                command.value = code;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return listCommand;
    }
}
