package db;

public class Command {

    public int port = -1;
    public String host = "";
    public String user = "";
    public String password = "";
    public String name = "";
    public String type = "mysql";
    public boolean express = false;
    public boolean jwt = false;
    public boolean modules = false;

    public static Command parse(String[] args){
        Command command = new Command();
        String option = "";
        for (String code: args) {
            if(code.substring(0,1).equals("-")){
                option = code;
                switch (option){
                    case "--express": case "-ex":
                        command.express = true; break;
                    case "--jwt":
                        command.jwt = true; break;
                    case "--modules": case "-m":
                        command.modules = true; break;
                }
            } else {
                switch (option){
                    case "--host": case "-h":
                        command.host = code; break;
                    case "--port":
                        command.port = Integer.parseInt(code); break;
                    case "--user": case "-u":
                        command.user = code; break;
                    case "--pass": case "-p":
                        command.password = code; break;
                    case "--name": case "-n":
                        command.name = code; break;
                    case "--type": case "-t":
                        command.type = code; break;
                }
            }
        }
        return command;
    }
}
