package tools.gnzlz.filetemplete;

import tools.gnzlz.command.result.ResultListCommand;
import tools.gnzlz.database.autocode.ACFormat;
import tools.gnzlz.database.autocode.ACCatalog;
import tools.gnzlz.database.autocode.ACDataBase;
import tools.gnzlz.database.model.interfaces.Dialect;
import tools.gnzlz.database.properties.PTConnection;
import tools.gnzlz.template.Template;
import tools.gnzlz.template.TemplateLoader;

import java.util.ArrayList;

public class TemplatesCatalog extends TemplateLoader<TemplatesCatalog> {

    /**
     * TemplatesCatalog
     */
    protected TemplatesCatalog(){
        this("","");
    }

    /**
     * TemplatesCatalog
     * @param path p
     */
    protected TemplatesCatalog(String path){
        this(path, "");
    }

    /**
     * TemplatesDatabase
     * @param path p
     * @param out o
     */
    protected TemplatesCatalog(String path, String out){
        super(path, out);
        TemplatesCatalog.setObjects(this);
    }

    /**
     * create
     */
    public static TemplatesCatalog create(){
        return new TemplatesCatalog();
    }

    /**
     * create
     * @param path p
     */
    public static TemplatesCatalog create(String path){
        return new TemplatesCatalog(path);
    }

    /**
     * create
     * @param path p
     * @param out o
     */
    public static TemplatesCatalog create(String path, String out){
        return new TemplatesCatalog(path, out);
    }

    /**
     * defaultObjects
     * @param template t
     */
    @Override
    protected void defaultObjects(Template template){
        TemplatesCatalog.setDefaultObjects(template);
    }

    /**
     * setDefaultObjects
     * @param template t
     */
    protected static void setDefaultObjects(Template template){
        template.object("classname", o -> {
            if (o instanceof Class c) {
                return c.getSimpleName();
            } else if(o != null) {
                return o.getClass().getSimpleName();
            }
            return "";
        });

        template.object("package", o -> {
            if (o instanceof Class c) {
                return c.getPackage().getName();
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
            if (o instanceof String s) {
                return s.isEmpty();
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
                return ACFormat.isDateFormat((String) o);
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
                .object("database.path", connection.path())
                .object("database.dialect", connection.dialect())
                .object("dialect.mysql", Dialect.MySQL)
                .object("dialect.sqlite", Dialect.SQLite)
                .object("dialect.postgresql", Dialect.PostgreSQL);
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
