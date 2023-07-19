package tools.gnzlz.filetemplete;

import tools.gnzlz.command.*;
import tools.gnzlz.command.Process;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.autocode.model.ACTable;
import tools.gnzlz.database.model.DBConfiguration;

public class Console {

    /*********************************
     * static
     *********************************/

    public static ListCommand listCommandDB = ListCommand.create();

    static {
        defaultCommands();
    }
    public static void defaultCommands() {
        listCommandDB.command("type").value(Option.option("mysql", "postgresql", "sqlite")).commands("--type", "-t");
        listCommandDB.command("path").value("").commands("--path", "-pt");
        listCommandDB.command("host").value("localhost").commands("--host", "-h");
        listCommandDB.command("port").value(-1).commands("--port");
        listCommandDB.command("user").value("root").commands("--user", "-u");
        listCommandDB.command("password").value("").commands("--pass", "-p");
        listCommandDB.command("name").value("").commands("--name", "-n");
        listCommandDB.command("modules").value(false).commands("--modules", "-m");
    }

    /*********************************
     * dbconfiguration
     *********************************/

    public static <T extends DBConfiguration> Class<? extends DBConfiguration> dbconfiguration(ResultListCommand command){
        Class<? extends DBConfiguration> c = null;
        if(command.string("type").equalsIgnoreCase("sqlite")){
            SQLite.initConfig(command);
            c = SQLite.class;
        } else {
            MySQL.initConfig(command);
            c = MySQL.class;
        }
        return c;
    }

    /*********************************
     * process
     *********************************/

    public static <T extends DBConfiguration> void process(ResultListCommand resultListCommand, String ... fileNames){
        Console.generate(dbconfiguration(resultListCommand), resultListCommand, fileNames);
    }
    public static <T extends DBConfiguration> void process(String[] args, ListCommand commands, String ... fileNames){
        ResultListCommand command = Process.process(args, commands);
        Console.generate(dbconfiguration(command), command, fileNames);
    }

    /*********************************
     * generate
     *********************************/

    public static <T extends DBConfiguration> void generate(Class<T> c, ResultListCommand command, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
        ListTemplates.commands(command);
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

    public static <T extends DBConfiguration> void processCatalog(ResultListCommand resultListCommand, String name, String ... fileNames) {
        Console.generateModel(dbconfiguration(resultListCommand), resultListCommand, name, fileNames);
    }

    /*********************************
     * generateCatalog
     *********************************/

    public static <T extends DBConfiguration> void generateCatalog(Class<T> c, ResultListCommand command, String name, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
        ListTemplates.commands(command);
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

    public static <T extends DBConfiguration> void processScheme(ResultListCommand resultListCommand, String name, String ... fileNames) {
        Console.generateModel(dbconfiguration(resultListCommand), resultListCommand, name, fileNames);
    }

    /*********************************
     * generateCatalog
     *********************************/

    public static <T extends DBConfiguration> void generateScheme(Class<T> c, ResultListCommand commands, String name, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, commands.string("name"));
        ListTemplates.commands(commands);
        dataBase.catalogs.forEach(catalog -> {
            catalog.schemes.stream()
                .filter(scheme -> name.isEmpty() || scheme.nameDefault().equalsIgnoreCase(commands.string(name)))
                .forEach(scheme -> {
                    ListTemplates.createScheme(dataBase, catalog, scheme, fileNames);
                }
            );
        });
    }

    /*********************************
     * processModel
     *********************************/

    public static <T extends DBConfiguration> void processModel(ResultListCommand resultListCommand, String name, String ... fileNames) {
        Console.generateModel(dbconfiguration(resultListCommand), resultListCommand, name, fileNames);
    }


    /*********************************
     * generateModel
     *********************************/

    public static <T extends DBConfiguration> void generateModel(Class<T> c, ResultListCommand commands, String name, String ... fileNames) {
        ACDataBase dataBase = ACDataBase.dataBase(c, commands.string("name"));
        ListTemplates.commands(commands);
        dataBase.catalogs.forEach(catalog -> {
            catalog.schemes.forEach(scheme -> {
                scheme.tables.stream()
                    .filter(table -> name.isEmpty() || table.name.equalsIgnoreCase(commands.string(name)))
                    .forEach(table -> {
                        ListTemplates.createModel(dataBase, catalog, scheme, table, fileNames);
                    }
                );
            });
        });
    }


}
