package tools.gnzlz.filetemplete;

import tools.gnzlz.command.ListCommand;
import tools.gnzlz.database.model.DBConfiguration;
import tools.gnzlz.database.properties.PropertiesConnection;
import tools.gnzlz.database.properties.PropertiesMigration;
import tools.gnzlz.database.properties.PropertiesModel;

public class MySQL extends DBConfiguration {

    public static String host = "localhost";
    public static int port = 3306;
    public static String user = "root";
    public static String password = "";
    public static String name = "";

    public static void initConfig(ListCommand command){
        if(!command.string("host").isEmpty()) host = command.string("host");
        if(command.integer("port") != -1) port = command.integer("port");
        if(!command.string("user").isEmpty()) user = command.string("user");
        if(!command.string("password").isEmpty()) password = command.string("password");
        if(!command.string("name").isEmpty()) name = command.string("name");
    }

    @Override
    protected void initConnection(PropertiesConnection connection) {
        connection
            .dialect(MySQL).protocol("jdbc:mysql:").host(host).port(port)
            .user(user).password(password).name(name)
            .property("useSSL","false").property("serverTimezone","UTC");
    }

    @Override
    protected void initMigration(PropertiesMigration migration) {

    }

    @Override
    protected void initModel(PropertiesModel model) {
        model.modelPackage("db.mysql.modelo").refresh(false);
    }
}
