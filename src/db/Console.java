package db;

import file.ListTemplates;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.autocode.model.ACTable;
import tools.gnzlz.database.model.DBConfiguration;

public class Console {
    public static void main(String[] args) {

        ListCommand command = Command.process(args);

        if(command.string("type").equalsIgnoreCase("sqltite")){

        } else {
            MySQL.initConfig(command);
            Console.generate(MySQL.class, command);
        }
    }

    public static <T extends DBConfiguration> void generate(Class<T> c, ListCommand command) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.string("name"));
        ListTemplates.commands(Command.listCommand);
        dataBase.catalogs.forEach(catalog -> {
            ListTemplates.create(dataBase, catalog);
            catalog.schemes.forEach(scheme -> {
                ListTemplates.create(scheme);
                for (ACTable table: scheme.tables){
                    ListTemplates.create(table);
                }
            });
        });
    }
}
