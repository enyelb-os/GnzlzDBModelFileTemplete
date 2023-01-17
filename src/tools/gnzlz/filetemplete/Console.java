package tools.gnzlz.filetemplete;

import tools.gnzlz.command.Command;
import tools.gnzlz.command.ListCommand;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.autocode.model.ACTable;
import tools.gnzlz.database.model.DBConfiguration;

public class Console {

    /*********************************
     * static
     *********************************/

    static {
        Command.command("host"      , "localhost" , "", "--host"      , "-h");
        Command.command("port"      , -1          , "", "--port");
        Command.command("user"      , "root"      , "", "--user"      , "-u");
        Command.command("password"  , ""          , "", "--pass"      , "-p");
        Command.command("name"      , ""          , "", "--name"      , "-n");
        Command.command("type"      , "mysql"     , "", "--type"      , "-t");
        Command.command("modules"   , false       , "", "--modules"   , "-m");
    }

    /*********************************
     * dbconfiguration
     *********************************/

    public static <T extends DBConfiguration> Class<? extends DBConfiguration> dbconfiguration(ListCommand command){
        Class<? extends DBConfiguration> c = null;
        if(command.string("type").equalsIgnoreCase("sqltite")){

        } else {
            MySQL.initConfig(command);
            c = MySQL.class;
        }
        return c;
    }

    /*********************************
     * process
     *********************************/

    public static <T extends DBConfiguration> void process(String[] args, String ... fileNames){
        ListCommand command = Command.process(args);
        Console.generate(dbconfiguration(command), command, fileNames);
    }

    /*********************************
     * generate
     *********************************/

    public static <T extends DBConfiguration> void generate(Class<T> c, ListCommand command, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
        ListTemplates.commands(Command.listCommand);
        dataBase.catalogs.forEach(catalog -> {
            ListTemplates.createCatalog(dataBase, catalog, fileNames);
            catalog.schemes.forEach(scheme -> {
                ListTemplates.createScheme(dataBase, catalog,scheme, fileNames);
                for (ACTable table: scheme.tables){
                    ListTemplates.createModel(dataBase, catalog, scheme, table, fileNames);
                }
            });
        });
    }

    /*********************************
     * processCatalog
     *********************************/

    public static <T extends DBConfiguration> void processCatalog(String[] args, String name, String ... fileNames) {
        ListCommand command = Command.process(args);
        Console.generateModel(dbconfiguration(command), command, name, fileNames);
    }

    /*********************************
     * generateCatalog
     *********************************/

    public static <T extends DBConfiguration> void generateCatalog(Class<T> c, ListCommand command, String name, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
        ListTemplates.commands(Command.listCommand);
        dataBase.catalogs.stream()
            .filter(catalog -> name.isEmpty() || catalog.name.equalsIgnoreCase(command.string(name)))
            .forEach(catalog -> {
                ListTemplates.createCatalog(dataBase, catalog, fileNames);
            }
        );
    }

    /*********************************
     * processScheme
     *********************************/

    public static <T extends DBConfiguration> void processScheme(String[] args, String name, String ... fileNames) {
        ListCommand command = Command.process(args);
        Console.generateModel(dbconfiguration(command), command, name, fileNames);
    }

    /*********************************
     * generateCatalog
     *********************************/

    public static <T extends DBConfiguration> void generateScheme(Class<T> c, ListCommand command, String name, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
        ListTemplates.commands(Command.listCommand);
        dataBase.catalogs.forEach(catalog -> {
            catalog.schemes.stream()
                .filter(scheme -> name.isEmpty() || scheme.nameDefault().equalsIgnoreCase(command.string(name)))
                .forEach(scheme -> {
                    ListTemplates.createScheme(dataBase, catalog, scheme, fileNames);
                }
            );
        });
    }

    /*********************************
     * processModel
     *********************************/

    public static <T extends DBConfiguration> void processModel(String[] args, String name, String ... fileNames) {
        ListCommand command = Command.process(args);
        Console.generateModel(dbconfiguration(command), command, name, fileNames);
    }


    /*********************************
     * generateModel
     *********************************/

    public static <T extends DBConfiguration> void generateModel(Class<T> c, ListCommand command, String name, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
        ListTemplates.commands(Command.listCommand);
        dataBase.catalogs.forEach(catalog -> {
            catalog.schemes.forEach(scheme -> {
                scheme.tables.stream()
                    .filter(table -> name.isEmpty() || table.name.equalsIgnoreCase(command.string(name)))
                    .forEach(table -> {
                        ListTemplates.createModel(dataBase, catalog, scheme, table, fileNames);
                    }
                );
            });
        });
    }


}
