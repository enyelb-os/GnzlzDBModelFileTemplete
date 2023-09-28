package tools.gnzlz.filetemplete;

import tools.gnzlz.command.result.ResultListCommand;
import tools.gnzlz.database.autocode.ACFormat;
import tools.gnzlz.database.autocode.model.ACCatalog;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.autocode.model.ACScheme;
import tools.gnzlz.database.autocode.model.ACTable;
import tools.gnzlz.database.model.DBConfiguration;
import tools.gnzlz.database.model.DBModel;
import tools.gnzlz.database.model.DBTable;
import tools.gnzlz.database.model.interfaces.Dialect;
import tools.gnzlz.database.properties.*;
import tools.gnzlz.database.query.model.Select;
import tools.gnzlz.template.template.Template;

import java.util.ArrayList;

public class ListTemplates {

    public static boolean isObjectsDBModel = false;

    /*********************************
     * Static
     *********************************/

    private static ArrayList<ListTemplates> templates = new ArrayList<ListTemplates>();

    /*********************************
     * vars
     *********************************/

    private String name;
    private Template template;
    private TypeTemplate type;

    /*********************************
     * constructor
     *********************************/

    private ListTemplates(String name,Template template, TypeTemplate type){
        this.name = name;
        this.template = template;
        ListTemplates.general(template);
        this.type = type;
    }

    /*********************************
     * file
     *********************************/

    public static void file(String name, String url, boolean internal, TypeTemplate type){
        templates.add(new ListTemplates(
            name,
            Template.file(url, internal),
            type
        ));
    }

    /*********************************
     * file
     *********************************/

    public static void file(String name, String url, TypeTemplate type){
        ListTemplates.file(name, url, true, type);
    }

    private static boolean existsFile(ListTemplates listTemplates, String ... fileNames){
        if(fileNames != null){
            if(fileNames.length == 0){
                return true;
            }
            for (String name: fileNames) {
                if(name.equals(listTemplates.name)){
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /*********************************
     * create
     *********************************/

    public static void createCatalog(ACDataBase dataBase, ACCatalog catalog, String ... fileNames){
        templates.stream()
            .filter(template -> template.type == TypeTemplate.CATALOG)
            .filter(listTemplates -> existsFile(listTemplates, fileNames))
            .forEach(template -> {
                database(template.template, dataBase);
                catalog(template.template, catalog);
                template.template.create();
            }
        );
    }

    /*********************************
     * create
     *********************************/

    public static void createScheme(ACDataBase dataBase, ACCatalog catalog, ACScheme scheme, String ... fileNames){
        objects(scheme);
        templates.stream()
            .filter(template -> template.type == TypeTemplate.SCHEME)
            .filter(listTemplates -> existsFile(listTemplates, fileNames))
            .forEach(template -> {
                database(template.template, dataBase);
                catalog(template.template, catalog);
                scheme(template.template, scheme);
                template.template.create();
            }
        );
    }

    /*********************************
     * create
     *********************************/

    public static void createModel(ACDataBase dataBase, ACCatalog catalog, ACScheme scheme, ACTable table, String ... fileNames){
        objects(table);
        templates.stream()
            .filter(template -> template.type == TypeTemplate.MODEL)
            .filter(listTemplates -> existsFile(listTemplates, fileNames))
            .forEach(template -> {
                database(template.template, dataBase);
                catalog(template.template, catalog);
                scheme(template.template, scheme);
                model(template.template, table);
                template.template.create();
            }
        );
    }


    /*********************************
     * commands
     *********************************/

    public static void commands(ResultListCommand commands){
        templates.stream().forEach(template ->{
            commands.listCommands((command -> {
                template.template.object("command." + command.name().toLowerCase(), command.value());
            }));
        });
    }

    /*********************************
     * object
     *********************************/

    public static void object(String name, Object object, TypeTemplate type){
        templates.stream().filter(template -> template.type == type)
        .forEach(template ->{
            template.template.object(name, object);
        });
    }


    /*********************************
     * objects
     *********************************/

    private static void objects(ACScheme scheme){
        templates.stream().filter(template -> template.type == TypeTemplate.SCHEME || template.type == TypeTemplate.MODEL)
        .forEach(template ->{
            scheme(template.template, scheme);
        });
    }

    /*********************************
     * objects
     *********************************/

    private static void objects(ACTable table){
        templates.stream().filter(template -> template.type == TypeTemplate.MODEL).forEach(template ->{
            model(template.template, table);
        });
    }

    /*********************************
     * commands
     *********************************/

    private static void commands(Template template, ResultListCommand commands){
        commands.listCommands((command -> {
            template.object("command." + command.name().toLowerCase(), command.value());
        }));
    }

    /*********************************
     * database
     *********************************/

    private static void database(Template template, ACDataBase dataBase){
        PTConnection connection = dataBase.configuration.connection().properties();
        template
            .object("database.name", connection.name())
            .object("database.user", connection.user())
            .object("database.password", connection.password())
            .object("database.host", connection.host())
            .object("database.port", connection.port())
            .object("database.dialect", connection.dialect())
            .object("dialect.mysql", Dialect.MySQL)
            .object("dialect.sqlite", Dialect.SQLite)
            .object("dialect.postgresql", Dialect.PostgreSQL);

        if(ListTemplates.isObjectsDBModel){
            template
                .object("DBModel", DBModel.class)
                .object("DataBase", dataBase)
                .object("DBTable", DBTable.class)
                .object("DBTable", DBTable.class)
                .object("Select", Select.class)
                .object("DBConfiguration", DBConfiguration.class)
                .object("PropertiesConnection", PropertiesConnection.class)
                .object("PropertiesTable", PropertiesTable.class)
                .object("PropertiesModel", PropertiesModel.class)
                .object("PropertiesMigration", PropertiesMigration.class)
                .object("ArrayList", ArrayList.class);
        }
    }

    /*********************************
     * catalog
     *********************************/

    private static void catalog(Template template, ACCatalog catalog){
        template
            .object("catalog", catalog);
    }

    /*********************************
     * scheme
     *********************************/

    private static void scheme(Template template, ACScheme scheme){
        template
            .object("scheme", scheme)
            .object("scheme.name", scheme.nameDefault());
    }

    /*********************************
     * model
     *********************************/

    private static void model(Template template, ACTable table){
        template
            .object("table", table)
            .object("table.imports", table.hasOneImports());

        if(ListTemplates.isObjectsDBModel){
            template
                .object("table.package", table.packegeName)
                .object("table.extra.imports", table.extraImports());
        }

    }

    /*********************************
     * model
     *********************************/

    private static void general(Template template){
        template.object("classname", o -> {
            if(o instanceof Class) {
                return ((Class<?>)o).getSimpleName();
            }else if(o instanceof Object){
                return o.getClass().getSimpleName();
            }
            return "";
        });

        template.object("package", o -> {
            if(o instanceof Class) {
                return ((Class<?>)o).getPackage().getName();
            }else if(o instanceof Object){
                return o.getClass().getPackage().getName();
            }
            return "";
        });

        template.object("camelcase", o -> {
            if(o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(ACFormat.camelCaseClass(o.toString()));
            }
            return "";
        });

        template.object("lowercamelcase", o -> {
            if(o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(ACFormat.camelCaseMethod(o.toString()));
            }
            return "";
        });

        template.object("uppercase", o -> {
            if(o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(o.toString()).toUpperCase();
            }
            return "";
        });

        template.object("lowercase", o -> {
            if(o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(o.toString()).toLowerCase();
            }
            return "";
        });

        template.object("empty", o -> {
            if(o instanceof String) {
                return ((String) o).isEmpty();
            } else if(o instanceof ArrayList){
                return ((ArrayList) o).isEmpty();
            }
            return true;
        });

        template.object("typeData", o -> {
            if (o instanceof String) {
                return ACFormat.typeData((String) o);
            }
            return "";
        });

        template.object("typeValue", o -> {
            if (o instanceof String) {
                return ACFormat.typeValue((String) o);
            }
            return "";
        });

        template.object("isDate", o -> {
            if (o instanceof String) {
                return ACFormat.dateFormat((String) o);
            }
            return false;
        });


        template.object("path", o -> {
            if (o instanceof String) {
                return ((String) o).replaceAll("[.]", "/");
            }
            return o;
        });
    }
}
