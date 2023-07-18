package tools.gnzlz.filetemplete;

import tools.gnzlz.command.ResultListCommand;
import tools.gnzlz.database.model.DBConfiguration;
import tools.gnzlz.database.properties.PropertiesConnection;
import tools.gnzlz.database.properties.PropertiesMigration;
import tools.gnzlz.database.properties.PropertiesModel;

public class SQLite extends DBConfiguration {

    public static String path = System.getProperty("user.dir").concat("/");
    public static String name = "database.db";

    public static void initConfig(ResultListCommand command){
        if(!command.string("path").isEmpty()) path = command.string("path").replaceAll("%20"," ");
        if(!command.string("name").isEmpty()) name = command.string("name");
    }

    @Override
    protected void initConnection(PropertiesConnection connection) {
        connection.dialect(SQLite).protocol("jdbc:sqlite:").path(path).name(name);
    }

    @Override
    protected void initMigration(PropertiesMigration migration) {

    }

    @Override
    protected void initModel(PropertiesModel model) {
        model.refresh(false);
    }
}
