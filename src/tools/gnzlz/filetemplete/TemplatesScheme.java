package tools.gnzlz.filetemplete;

import tools.gnzlz.database.autocode.ACScheme;
import tools.gnzlz.template.Template;
import tools.gnzlz.template.TemplateLoader;

public class TemplatesScheme extends TemplateLoader<TemplatesScheme> {

    /**
     * TemplatesScheme
     */
    protected TemplatesScheme(){
        this("","");
    }

    /**
     * TemplatesScheme
     * @param path p
     */
    protected TemplatesScheme(String path){
        this(path, "");
    }

    /**
     * TemplatesScheme
     * @param path p
     * @param out o
     */
    protected TemplatesScheme(String path, String out){
        super(path, out);
        TemplatesCatalog.setObjects(this);
        TemplatesScheme.setObjects(this);
    }

    /**
     * create
     */
    public static TemplatesScheme create(){
        return new TemplatesScheme();
    }

    /**
     * create
     * @param path p
     */
    public static TemplatesScheme create(String path){
        return new TemplatesScheme(path);
    }

    /**
     * create
     * @param path p
     * @param out o
     */
    public static TemplatesScheme create(String path, String out){
        return new TemplatesScheme(path, out);
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
        templatesBase.objects(ACScheme.class, (template, scheme) -> {
            template
                .object("scheme", scheme)
                .object("scheme.name", scheme.nameDefault());
        });
    }
}
