package tools.gnzlz.filetemplete;

import tools.gnzlz.command.command.functional.FunctionRequiredCommand;
import tools.gnzlz.command.command.functional.FunctionValidCommand;
import tools.gnzlz.command.command.object.ListCommand;
import tools.gnzlz.command.process.Process;
import tools.gnzlz.command.result.ResultListCommand;
import tools.gnzlz.command.CommandInteger;
import tools.gnzlz.command.CommandOptionString;
import tools.gnzlz.command.CommandString;
import tools.gnzlz.database.autocode.ACDataBase;
import tools.gnzlz.database.model.DBConfiguration;
import tools.gnzlz.filetemplete.properties.Properties;
import tools.gnzlz.system.ansi.Color;
import tools.gnzlz.system.io.SystemIO;
import tools.gnzlz.template.TemplateLoader;
import tools.gnzlz.template.TemplateManager;
import tools.gnzlz.template.TemplatesNone;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Console {

    /**
     * REQUIRED_DB_FILE
     */
    private final static FunctionRequiredCommand REQUIRED_DB_FILE = (data) -> data.list.string("type").equalsIgnoreCase("sqlite");

    /**
     * REQUIRED_DB_FILE
     */
    private final static FunctionRequiredCommand REQUIRED_DB_SERVER = (data) -> data.list.string("type").equalsIgnoreCase("mysql") || data.list.string("type").equalsIgnoreCase("postgresql");

    /**
     * TYPE
     */
    public final static CommandOptionString TYPE = CommandOptionString
            .create("type")
            .commands("--type", "-t")
            .message("Type connection")
            .required()
            .option("mysql", "postgresql", "sqlite");

    /**
     * HOST
     */
    public final static CommandString HOST = CommandString
            .create("host")
            .commands("--host", "-h")
            .message("host")
            .required(REQUIRED_DB_SERVER)
            .value("localhost");

    /**
     * PORT
     */
    public final static CommandInteger PORT = CommandInteger
            .create("port")
            .commands("--port", "-pr")
            .message("port")
            .required(REQUIRED_DB_SERVER)
            .value(-1);

    /**
     * USER
     */
    public final static CommandString USER = CommandString
            .create("user")
            .message("user")
            .required(REQUIRED_DB_SERVER)
            .value("root")
            .commands("--user", "-u");

    /**
     * PASSWORD
     */
    public final static CommandString PASSWORD = CommandString
            .create("password")
            .commands("--pass", "-p")
            .message("password")
            .required(REQUIRED_DB_SERVER)
            .value("");

    /**
     * NAME
     */
    public final static CommandString NAME = CommandString
            .create("name")
            .commands("--name", "-n")
            .message("name")
            .required();

    /**
     * PATH
     */
    public final static CommandString PATH = CommandString
            .create("path")
            .message("Path file db")
            .required(REQUIRED_DB_FILE)
            .valid(FunctionValidCommand.FILE)
            .commands("--path", "-pt");

    /**
     * listCommandDB
     */
    public static ListCommand listCommandDB = ListCommand.create()
            .addCommand(TYPE, PATH, HOST, PORT, USER, PASSWORD, NAME);


    /**
     * dbConfiguration
     * @param command c
     */
    public static Class<? extends DBConfiguration> dbConfiguration(ResultListCommand command){
        Class<? extends DBConfiguration> c = null;
        if(command.string("type").equalsIgnoreCase("sqlite")){
            SQLite.initConfig(command);
            c = SQLite.class;
        } else if (command.string("type").equalsIgnoreCase("mysql")){
            MySQL.initConfig(command);
            c = MySQL.class;
        }
        return c;
    }

    /**
     * process
     * @param resultListCommand r
     * @param manager m
     * @param properties f
     */
    public static void process(ResultListCommand resultListCommand, TemplateManager manager, Properties properties){
        Console.generate(dbConfiguration(resultListCommand), resultListCommand, manager.templates(), properties);
    }

    /**
     * process
     * @param resultListCommand r
     * @param manager m
     */
    public static void process(ResultListCommand resultListCommand, TemplateManager manager){
        Console.process(resultListCommand, manager, Properties.create());
    }

    /**
     * process
     * @param commands c
     * @param manager m
     * @param properties properties
     */
    public static void process(String[] args, ListCommand commands, TemplateManager manager, Properties properties){
        ResultListCommand command = Process.argsAndQuestions(args, commands);
        Console.generate(dbConfiguration(command), command, manager.templates(), properties);
    }

    /**
     * process
     * @param commands c
     * @param manager m
     */
    public static void process(String[] args, ListCommand commands, TemplateManager manager){
        Console.process(args, commands, manager, Properties.create());
    }

    /**
     * generate
     * @param <T> t
     * @param command c
     * @param templates t
     * @param properties properties
     */
    public static <T extends DBConfiguration> void generate(Class<T> c, ResultListCommand command, ArrayList<TemplateLoader<?>> templates, Properties properties) {
        boolean createCatalog = true;
        boolean createScheme = true;
        boolean createModel = true;
        boolean createNone = true;
        final StringBuilder names = new StringBuilder();
        properties.properties().forEach(property -> {
            if (property.name().equals("templates")) {
                if (property.object() instanceof String template) {
                    if (!names.isEmpty()) {
                        names.append(",");
                    }
                    names.append(template);
                }
            }
        });
        if (c != null) {
            ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
            TemplateManager.processObjects(templates, command, dataBase);

            dataBase.catalogs.forEach(catalog -> {

                TemplateManager.processObjects(templates, catalog);
                TemplateManager.processTemplate(names, createCatalog, templates, t -> t instanceof TemplatesCatalog);

                catalog.schemes.forEach(scheme -> {

                    TemplateManager.processObjects(templates, scheme);
                    TemplateManager.processTemplate(names, createScheme, templates, t -> t instanceof TemplatesScheme);

                    scheme.tables.forEach(table -> {
                        TemplateManager.processTemplate(names, createModel, templates, t -> t instanceof TemplatesModel, table);
                    });
                });
            });
        } else {
            TemplateManager.processObjects(templates, command);
            TemplateManager.processTemplate(names, createNone, templates, t -> t instanceof TemplatesNone);
        }

        SystemIO.OUT.println(Color.RED.print("Press enter to continue"));
        SystemIO.INP.process();
    }
}
