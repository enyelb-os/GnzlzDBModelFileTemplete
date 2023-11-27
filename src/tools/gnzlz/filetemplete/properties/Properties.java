package tools.gnzlz.filetemplete.properties;
import java.util.ArrayList;

public class Properties {
    /**
     * Properties
     */

    private final ArrayList<Property<?>> properties;

    /**
     * Properties
     */
    protected Properties(){
        this.properties = new ArrayList<>();
    }

    /**
     * create
     */
    public static Properties create(){
        return new Properties();
    }

    /**
     * add
     * @param <Type> type
     * @param name name
     * @param type type
     */
    public <Type> Properties add(String name, Type type){
        this.properties.add(new Property<Type>(name, type));
        return this;
    }

    /**
     * properties
     */
    public ArrayList<Property<?>> properties(){
        return this.properties;
    }

}
