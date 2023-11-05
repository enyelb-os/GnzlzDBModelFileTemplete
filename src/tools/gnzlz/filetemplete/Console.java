package tools.gnzlz.filetemplete;

import tools.gnzlz.command.command.Command;
import tools.gnzlz.command.command.functional.FunctionRequiredCommand;
import tools.gnzlz.command.command.object.ListCommand;
import tools.gnzlz.command.command.type.*;
import tools.gnzlz.command.process.Process;
import tools.gnzlz.command.result.ResultListCommand;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.model.DBConfiguration;
import tools.gnzlz.template.template.TemplateLoader;
import tools.gnzlz.template.template.TemplateManager;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Console {

    /**
     * REQUIRED_DB_FILE
     */
    private final static FunctionRequiredCommand REQUIRED_DB_FILE = (commands) -> commands.string("type").equalsIgnoreCase("sqlite");

    /**
     * REQUIRED_DB_FILE
     */
    private final static FunctionRequiredCommand REQUIRED_DB_SERVER = (commands) -> {
        System.out.println(commands.string("type").equalsIgnoreCase("mysql") || commands.string("type").equalsIgnoreCase("postgresql"));
        return commands.string("type").equalsIgnoreCase("mysql") || commands.string("type").equalsIgnoreCase("postgresql");
    };

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
    public final static Command HOST = CommandString
            .create("host")
            .commands("--host", "-h")
            .message("host")
            .required(REQUIRED_DB_SERVER)
            .value("localhost");

    /**
     * PORT
     */
    public final static Command PORT = CommandInteger
            .create("port")
            .commands("--port")
            .message("port")
            .required(REQUIRED_DB_SERVER)
            .value(-1);

    /**
     * USER
     */
    public final static Command USER = CommandString
            .create("user")
            .message("user")
            .required(REQUIRED_DB_SERVER)
            .value("root")
            .commands("--user", "-u");

    /**
     * PASSWORD
     */
    public final static Command PASSWORD = CommandString
            .create("password")
            .commands("--pass", "-p")
            .message("password")
            .required(REQUIRED_DB_SERVER)
            .value("");

    /**
     * NAME
     */
    public final static Command NAME = CommandString
            .create("name")
            .commands("--name", "-n")
            .message("name")
            .required();

    /**
     * PATH
     */
    public final static Command PATH = CommandString
            .create("path")
            .message("Path file db")
            .required(REQUIRED_DB_FILE)
            .commands("--path", "-pt");

    /**
     * listCommandDB
     */
    public static ListCommand listCommandDB = ListCommand.create()
            .addCommand(TYPE, PATH, HOST, PORT, USER, PASSWORD, NAME);


    /**
     * dbConfiguration
     * @param <T> t
     * @param command c
     */
    public static <T extends DBConfiguration> Class<? extends DBConfiguration> dbConfiguration(ResultListCommand command){
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

    /**
     * process
     * @param resultListCommand r
     * @param manager m
     * @param fileNames f
     */
    public static void process(ResultListCommand resultListCommand, TemplateManager manager, String... fileNames){
        Console.generate(dbConfiguration(resultListCommand), resultListCommand, manager.templates(), fileNames);
    }

    /**
     * process
     * @param commands c
     * @param manager m
     * @param fileNames f
     */
    public static void process(String[] args, ListCommand commands, TemplateManager manager, String... fileNames){
        ResultListCommand command = Process.argsAndQuestions(args, commands);
        Console.generate(dbConfiguration(command), command, manager.templates(), fileNames);
    }

    /**
     * generate
     * @param <T> t
     * @param command c
     * @param templates t
     * @param fileNames f
     */
    public static <T extends DBConfiguration> void generate(Class<T> c, ResultListCommand command, ArrayList<TemplateLoader> templates, String... fileNames) {

        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));

        boolean createCatalog = true;
        boolean createScheme = true;
        boolean createModel = true;
        String names = "";

        Console.processObjects(templates, command, dataBase);

        dataBase.catalogs.forEach(catalog -> {

            Console.processObjects(templates, catalog);
            Console.processTemplate(names, createCatalog, templates, t -> t instanceof TemplatesDatabase);

            catalog.schemes.forEach(scheme -> {

                Console.processObjects(templates, scheme);
                Console.processTemplate(names, createScheme, templates, t -> t instanceof TemplatesScheme);

                scheme.tables.forEach(table -> {

                    Console.processObjects(templates, table);
                    Console.processTemplate(names, createModel, templates, t -> t instanceof TemplatesModel, table);
                });
            });
        });
    }

    /**
     * processTemplate
     * @param names n
     * @param templates t
     * @param predicate p
     * @param objects o
     */
    private static void processTemplate(String names, boolean process, ArrayList<TemplateLoader> templates, Predicate<? super TemplateLoader> predicate, Object ... objects) {
        if (process) {
            templates.stream().filter(predicate).forEach(template -> {
                template.process(names,objects);
            });
        }
    }

    /**
     * processObjects
     * @param templates t
     * @param objects o
     */
    private static void processObjects(ArrayList<TemplateLoader> templates, Object ... objects) {
        templates.forEach(template -> {
            template.objects(objects);
        });
    }
}
