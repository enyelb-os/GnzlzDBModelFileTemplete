package tools.gnzlz.filetemplete;

import tools.gnzlz.database.autocode.ACFormat;
import tools.gnzlz.database.autocode.ACTable;
import tools.gnzlz.template.Template;
import tools.gnzlz.template.TemplateLoader;

import java.util.ArrayList;

public class TemplatesModel extends TemplateLoader<TemplatesModel> {

    /**
     * TemplatesModel
     */
    protected TemplatesModel(){
        this("","");
    }

    /**
     * TemplatesModel
     * @param path p
     */
    protected TemplatesModel(String path){
        this(path, "");
    }

    /**
     * TemplatesModel
     * @param path p
     * @param out o
     */
    protected TemplatesModel(String path, String out){
        super(path, out);
        TemplatesCatalog.setObjects(this);
        TemplatesScheme.setObjects(this);
        TemplatesModel.setObjects(this);
    }

    /**
     * create
     */
    public static TemplatesModel create(){
        return new TemplatesModel();
    }

    /**
     * create
     * @param path p
     */
    public static TemplatesModel create(String path){
        return new TemplatesModel(path);
    }

    /**
     * create
     * @param path p
     * @param out o
     */
    public static TemplatesModel create(String path, String out){
        return new TemplatesModel(path, out);
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
     * setObjects
     * @param templatesBase t
     */
    protected static void setObjects(TemplateLoader<?> templatesBase) {
        templatesBase.objects(ACTable.class, (template, table) -> {
            ArrayList<String> imports = new ArrayList<>();
            table.columns.forEach(column -> {
                String newImport = ACFormat.imports(column.type);
                if(!imports.contains(newImport)) {
                    imports.add(newImport);
                }
            });
            template
                .object("table", table)
                .object("table.imports", imports);
        });
    }
}
