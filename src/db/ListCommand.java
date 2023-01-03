package db;

import tools.gnzlz.template.Template.Template;

import java.util.ArrayList;

public class ListCommand {

    private ArrayList<Command> listCommands;
    ListCommand(ArrayList listCommands){
        this.listCommands = listCommands;
    }

    public Object get(String name){
        for (Command command: listCommands) {
            if(command.name.equals(name)){
                return command.value;
            }
        }
        return null;
    }

    public String string(String name){
        for (Command command: listCommands) {
            if(command.name.equals(name)){
                return command.value.toString();
            }
        }
        return "";
    }

    public int integer(String name){
        for (Command command: listCommands) {
            if(command.name.equals(name) && command.value instanceof Integer){
                return (int) command.value;
            }
        }
        return -1;
    }

    public void addCommandsTemplete(Template template){
        for (Command command: listCommands) {
            template.object("command." + command.name.toLowerCase(), command.value);
        }
    }
}
