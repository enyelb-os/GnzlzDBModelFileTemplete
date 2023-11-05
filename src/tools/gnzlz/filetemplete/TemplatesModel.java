package tools.gnzlz.filetemplete;

import tools.gnzlz.database.autocode.model.ACTable;
import tools.gnzlz.template.Template;
import tools.gnzlz.template.TemplateLoader;

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
        TemplatesDatabase.setObjects(this);
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
        TemplatesDatabase.setDefaultObjects(template);
    }

    /**
     * setObjects
     * @param templatesBase t
     */
    protected static void setObjects(TemplateLoader<?> templatesBase) {
        templatesBase.objects(ACTable.class, (template, table) -> {
            template
                .object("table", table)
                .object("table.imports", table.hasOneImports());

            if(TemplatesDatabase.isObjectsDBModel){
                template
                    .object("table.package", table.packegeName)
                    .object("table.extra.imports", table.extraImports());
            }
        });
    }
}
