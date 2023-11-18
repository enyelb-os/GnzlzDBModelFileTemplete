package tools.gnzlz.filetemplete;

import tools.gnzlz.command.result.ResultListCommand;
import tools.gnzlz.database.autocode.ACFormat;
import tools.gnzlz.database.autocode.model.ACCatalog;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.model.DBConfiguration;
import tools.gnzlz.database.model.DBModel;
import tools.gnzlz.database.model.DBTable;
import tools.gnzlz.database.model.interfaces.Dialect;
import tools.gnzlz.database.properties.*;
import tools.gnzlz.database.query.model.Select;
import tools.gnzlz.template.Template;
import tools.gnzlz.template.TemplateLoader;

import java.util.ArrayList;

public class TemplatesDatabase extends TemplateLoader<TemplatesDatabase> {

    /**
     * isObjectsDBModel
     */
    public static boolean isObjectsDBModel = false;

    /**
     * TemplatesDatabase
     */
    protected TemplatesDatabase(){
        this("","");
    }

    /**
     * TemplatesDatabase
     * @param path p
     */
    protected TemplatesDatabase(String path){
        this(path, "");
    }

    /**
     * TemplatesDatabase
     * @param path p
     * @param out o
     */
    protected TemplatesDatabase(String path, String out){
        super(path, out);
        TemplatesDatabase.setObjects(this);
    }

    /**
     * create
     */
    public static TemplatesDatabase create(){
        return new TemplatesDatabase();
    }

    /**
     * create
     * @param path p
     */
    public static TemplatesDatabase create(String path){
        return new TemplatesDatabase(path);
    }

    /**
     * create
     * @param path p
     * @param out o
     */
    public static TemplatesDatabase create(String path, String out){
        return new TemplatesDatabase(path, out);
    }

    /**
     * defaultObjects
     * @param template t
     */
    @Override
    protected void defaultObjects(Template template){
        TemplatesDatabase.setDefaultObjects(template);
    }

    /**
     * setDefaultObjects
     * @param template t
     */
    protected static void setDefaultObjects(Template template){
        template.object("classname", o -> {
            if (o instanceof Class) {
                return ((Class<?>)o).getSimpleName();
            } else if(o != null) {
                return o.getClass().getSimpleName();
            }
            return "";
        });

        template.object("package", o -> {
            if (o instanceof Class) {
                return ((Class<?>)o).getPackage().getName();
            } else if(o != null) {
                return o.getClass().getPackage().getName();
            }
            return "";
        });

        template.object("camelcase", o -> {
            if (o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(ACFormat.camelCaseClass(o.toString()));
            }
            return "";
        });

        template.object("lowercamelcase", o -> {
            if (o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(ACFormat.camelCaseMethod(o.toString()));
            }
            return "";
        });

        template.object("uppercase", o -> {
            if (o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(o.toString()).toUpperCase();
            }
            return "";
        });

        template.object("lowercase", o -> {
            if (o instanceof String || o instanceof Dialect) {
                return ACFormat.beginValidNumber(o.toString()).toLowerCase();
            }
            return "";
        });

        template.object("empty", o -> {
            if (o instanceof String) {
                return ((String) o).isEmpty();
            } else if(o instanceof ArrayList) {
                return ((ArrayList<?>) o).isEmpty();
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

    /**
     * setObjects
     * @param templatesBase t
     */
    protected static void setObjects(TemplateLoader<?> templatesBase) {
        templatesBase.objects(ACDataBase.class, (template, dataBase) -> {
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

            if(TemplatesDatabase.isObjectsDBModel){
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
        });

        templatesBase.objects(ResultListCommand.class, (template, commands) -> {
            commands.listCommands((command -> {
                template.object("command." + command.name().toLowerCase(), command.value());
            }));
        });

        templatesBase.objects(ACCatalog.class, (template, catalog) -> {
            template.object("catalog", catalog);
        });
    }
}
