package tools.gnzlz.filetemplete;

import tools.gnzlz.database.autocode.model.ACScheme;
import tools.gnzlz.template.template.Template;
import tools.gnzlz.template.template.type.TemplatesBase;

public class TemplatesScheme extends TemplatesBase<TemplatesScheme> {

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
        TemplatesDatabase.setObjects(this);
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
        TemplatesDatabase.setDefaultObjects(template);
    }

    /**
     * setObjects
     * @param templatesBase t
     */
    protected static void setObjects(TemplatesBase<?> templatesBase) {
        templatesBase.objects(ACScheme.class, (template, scheme) -> {
            template
                .object("scheme", scheme)
                .object("scheme.name", scheme.nameDefault());
        });
    }
}
