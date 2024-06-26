package tools.gnzlz.filetemplete;

import tools.gnzlz.command.result.ResultListCommand;
import tools.gnzlz.database.autocode.*;
import tools.gnzlz.database.model.interfaces.Dialect;
import tools.gnzlz.database.properties.PTConnection;
import tools.gnzlz.template.TemplateLoader;

import java.util.ArrayList;

public class TemplateObjects {

    /**
     * setObjectsCommands
     */
    public static tools.gnzlz.template.TemplateObjects setObjectsCommands = (TemplateLoader<?> templatesBase) -> {
        templatesBase.addObjects(ResultListCommand.class, (template, commands) -> {
            commands.listCommands((command -> {
                template.object("command." + command.name().toLowerCase(), command.value());
            }));
        });
    };

    /**
     * setObjectsCatalog
     */
    public static tools.gnzlz.template.TemplateObjects setObjectsCatalog = (TemplateLoader<?> templatesBase) -> {
        setObjectsCommands.setObjects(templatesBase);

        templatesBase.addObjects(ACDataBase.class, (template, dataBase) -> {
            PTConnection connection = dataBase.configuration.connection().properties();
            template
                .object("database.name", connection.name())
                .object("database.user", connection.user())
                .object("database.password", connection.password())
                .object("database.host", connection.host())
                .object("database.port", connection.port())
                .object("database.path", connection.path())
                .object("database.dialect", connection.dialect())
                .object("dialect.mysql", Dialect.MySQL)
                .object("dialect.sqlite", Dialect.SQLite)
                .object("dialect.postgresql", Dialect.PostgreSQL);
        });

        templatesBase.addObjects(ACCatalog.class, (template, catalog) -> {
            template.object("catalog", catalog);
        });
    };

    /**
     * setObjectsScheme
     */
    public static tools.gnzlz.template.TemplateObjects setObjectsScheme = (TemplateLoader<?> templatesBase) -> {
        setObjectsCatalog.setObjects(templatesBase);

        templatesBase.addObjects(ACScheme.class, (template, scheme) -> {
            template
                .object("scheme", scheme)
                .object("scheme.name", scheme.nameDefault());
        });
    };

    /**
     * setObjectsModel
     */
    public static tools.gnzlz.template.TemplateObjects setObjectsModel = (TemplateLoader<?> templatesBase) -> {
        setObjectsScheme.setObjects(templatesBase);

        templatesBase.addObjects(ACTable.class, (template, table) -> {
            ArrayList<String> imports = new ArrayList<>();
            table.columns.forEach(column -> {
                String newImport = ACFormat.imports(column.type.type);
                if(!imports.contains(newImport)) {
                    imports.add(newImport);
                }
            });
            template
                .object("table", table)
                .object("table.imports", imports);
        });
    };
}
