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
        TemplateObjects.setObjectsCatalog(this);
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
}
