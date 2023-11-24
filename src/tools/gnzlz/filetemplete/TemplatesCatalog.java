package tools.gnzlz.filetemplete;

import tools.gnzlz.command.result.ResultListCommand;
import tools.gnzlz.database.autocode.ACFormat;
import tools.gnzlz.database.autocode.ACCatalog;
import tools.gnzlz.database.autocode.ACDataBase;
import tools.gnzlz.database.model.interfaces.Dialect;
import tools.gnzlz.database.properties.PTConnection;
import tools.gnzlz.template.Template;
import tools.gnzlz.template.TemplateLoader;

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
     * TemplatesCatalog
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
        super.defaultObjects(template);
        TemplatesCatalog.setDefaultObjects(template);
    }

    /**
     * setDefaultObjects
     * @param template t
     */
    protected static void setDefaultObjects(Template template){

        template.object("typeData", o -> {
            if (o instanceof String s) {
                return ACFormat.typeData(s);
            }
            return "";
        });

        template.object("typeValue", o -> {
            if (o instanceof String s) {
                return ACFormat.typeValue(s);
            }
            return "";
        });

        template.object("isDate", o -> {
            if (o instanceof String s) {
                return ACFormat.isDateFormat(s);
            }
            return false;
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
