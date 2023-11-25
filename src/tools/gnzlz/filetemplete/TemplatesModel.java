package tools.gnzlz.filetemplete;

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
        TemplateObjects.setObjectsModel(this);
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
}
