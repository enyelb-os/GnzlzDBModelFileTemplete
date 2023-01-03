package file;

import command.ListCommand;
import tools.gnzlz.database.autocode.ACFormat;
import tools.gnzlz.database.autocode.model.ACCatalog;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.autocode.model.ACScheme;
import tools.gnzlz.database.autocode.model.ACTable;
import tools.gnzlz.database.model.interfaces.Dialect;
import tools.gnzlz.database.properties.PTConnection;
import tools.gnzlz.template.Template.Template;

import java.util.ArrayList;

public class ListTemplates {

    private static ArrayList<ListTemplates> templates = new ArrayList<ListTemplates>();

    private Template template;
    private TypeTemplate type;

    private ListTemplates(Template template, TypeTemplate type){
        this.template = template;
        ListTemplates.general(template);
        this.type = type;
    }

    public static void file(String url, boolean internal, TypeTemplate type){
        templates.add(new ListTemplates(
            Template.file(url, internal),
            type
        ));
    }

    public static void file(String url, TypeTemplate type){
        ListTemplates.file(url, true, type);
    }

    public static void create(ACDataBase dataBase, ACCatalog catalog){
        objects(dataBase);
        objects(catalog);
        templates.stream().filter(template -> template.type == TypeTemplate.CATALOG).forEach(template ->{
            template.template.create();
        });
    }

    public static void create(ACScheme scheme){
        objects(scheme);
        templates.stream().filter(template -> template.type == TypeTemplate.SCHEME).forEach(template ->{
            template.template.create();
        });
    }

    public static void create(ACTable table){
        objects(table);
        templates.stream().filter(template -> template.type == TypeTemplate.MODEL).forEach(template ->{
            template.template.create();
        });
    }

    public static void commands(ListCommand commands){
        templates.stream().forEach(template ->{
            commands.listCommands((command -> {
                template.template.object("command." + command.name().toLowerCase(), command.value());
            }));
        });

    }

    public static void object(String name, Object object, TypeTemplate type){
        templates.stream().filter(template -> template.type == type)
        .forEach(template ->{
            template.template.object(name, object);
        });
    }

    private static void objects(ACDataBase dataBase){
        templates.stream().forEach(template ->{
            database(template.template, dataBase);
        });
    }

    private static void objects(ACCatalog catalog){
        templates.stream()//.filter(template -> template.type == TypeTemplate.CATALOG)
        .forEach(template ->{
            catalog(template.template, catalog);
        });
    }

    private static void objects(ACScheme scheme){
        templates.stream().filter(template -> template.type == TypeTemplate.SCHEME || template.type == TypeTemplate.MODEL)
        .forEach(template ->{
            scheme(template.template, scheme);
        });
    }

    private static void objects(ACTable table){
        templates.stream().filter(template -> template.type == TypeTemplate.MODEL).forEach(template ->{
            model(template.template, table);
        });
    }

    private static void commands(Template template, ListCommand commands){
        commands.listCommands((command -> {
            template.object("command." + command.name().toLowerCase(), command.value());
        }));
    }

    private static void database(Template template, ACDataBase dataBase){
        PTConnection connection = dataBase.configuration.connection().properties();
        template
            .object("database.name", connection.database())
            .object("database.user", connection.user())
            .object("database.password", connection.password())
            .object("database.host", connection.host())
            .object("database.port", connection.port())
            .object("database.dialect", connection.dialect())
            .object("dialect.mysql", Dialect.MySQL)
            .object("dialect.sqlite", Dialect.SQLite)
            .object("dialect.postgresql", Dialect.PostgreSQL);
    }
    private static void catalog(Template template, ACCatalog catalog){
        template
            .object("catalog", catalog);
    }

    private static void scheme(Template template, ACScheme scheme){
        template
            .object("scheme", scheme)
            .object("scheme.name", scheme.nameDefault());
    }

    private static void model(Template template, ACTable table){
        template
            .object("table", table)
            .object("table", table)
            .object("table.imports", table.hasOneNameImports());
    }

    private static void general(Template template){
        template.object("camelcase", o -> {
            if(o instanceof String) {
                return ACFormat.camelCaseClass((String) o);
            }
            return "";
        });

        template.object("empty", o -> {
            if(o instanceof String) {
                return ((String) o).isEmpty();
            }
            return true;
        });
    }
}
