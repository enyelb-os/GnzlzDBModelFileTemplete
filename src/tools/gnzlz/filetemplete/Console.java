package tools.gnzlz.filetemplete;

import tools.gnzlz.command.Process;
import tools.gnzlz.command.command.object.ListCommand;
import tools.gnzlz.command.command.type.*;
import tools.gnzlz.command.result.ResultListCommand;
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
        listCommandDB.addCommand(CommandOptionString.create("type").option("mysql", "postgresql", "sqlite").commands("--type", "-t"));
        listCommandDB.addCommand(CommandString.create("path").value("").commands("--path", "-pt"));
        listCommandDB.addCommand(CommandString.create("host").value("localhost").commands("--host", "-h"));
        listCommandDB.addCommand(CommandInteger.create("port").value(-1).commands("--port"));
        listCommandDB.addCommand(CommandString.create("user").value("root").commands("--user", "-u"));
        listCommandDB.addCommand(CommandString.create("password").value("").commands("--pass", "-p"));
        listCommandDB.addCommand(CommandString.create("name").value("").commands("--name", "-n"));
        listCommandDB.addCommand(CommandBoolean.create("modules").value(false).commands("--modules", "-m"));
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

    public static <T extends DBConfiguration> void process(ResultListCommand resultListCommand, java.lang.String... fileNames){
        Console.generate(dbconfiguration(resultListCommand), resultListCommand, fileNames);
    }
    public static <T extends DBConfiguration> void process(java.lang.String[] args, ListCommand commands, java.lang.String... fileNames){
        ResultListCommand command = Process.argsAndQuestions(args, commands);
        Console.generate(dbconfiguration(command), command, fileNames);
    }

    /*********************************
     * generate
     *********************************/

    public static <T extends DBConfiguration> void generate(Class<T> c, ResultListCommand command, java.lang.String... fileNames) {
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

    public static <T extends DBConfiguration> void processCatalog(ResultListCommand resultListCommand, java.lang.String name, java.lang.String... fileNames) {
        Console.generateModel(dbconfiguration(resultListCommand), resultListCommand, name, fileNames);
    }

    /*********************************
     * generateCatalog
     *********************************/

    public static <T extends DBConfiguration> void generateCatalog(Class<T> c, ResultListCommand command, java.lang.String name, java.lang.String... fileNames) {
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

    public static <T extends DBConfiguration> void processScheme(ResultListCommand resultListCommand, java.lang.String name, java.lang.String... fileNames) {
        Console.generateModel(dbconfiguration(resultListCommand), resultListCommand, name, fileNames);
    }

    /*********************************
     * generateCatalog
     *********************************/

    public static <T extends DBConfiguration> void generateScheme(Class<T> c, ResultListCommand commands, java.lang.String name, java.lang.String... fileNames) {
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

    public static <T extends DBConfiguration> void processModel(ResultListCommand resultListCommand, java.lang.String name, java.lang.String... fileNames) {
        Console.generateModel(dbconfiguration(resultListCommand), resultListCommand, name, fileNames);
    }


    /*********************************
     * generateModel
     *********************************/

    public static <T extends DBConfiguration> void generateModel(Class<T> c, ResultListCommand commands, java.lang.String name, java.lang.String... fileNames) {
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
