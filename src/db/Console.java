package db;

import tools.gnzlz.javascript.express.controllers.ControllerSequelizeBase;
import tools.gnzlz.javascript.express.controllers.ControllerSequelize;
import tools.gnzlz.javascript.express.controllers.ControllerSequelizeValidation;
import tools.gnzlz.javascript.express.controllers.ControllerSequelizeLogin;
import tools.gnzlz.javascript.middlewares.JWTBase;
import tools.gnzlz.javascript.express.routes.RouteSequelizeUnited;
import tools.gnzlz.javascript.express.routes.RouteSequelizeLogin;
import tools.gnzlz.javascript.express.routes.RouteSequelizeBase;
import tools.gnzlz.javascript.express.routes.RouteSequelize;
import tools.gnzlz.javascript.sequelize.models.Database;
import tools.gnzlz.javascript.sequelize.models.ModelBase;
import tools.gnzlz.javascript.sequelize.models.Model;
import tools.gnzlz.javascript.sequelize.models.ModelValidation;
import tools.gnzlz.javascript.sequelize.repositories.RepositoryUtils;
import tools.gnzlz.javascript.sequelize.repositories.RepositoryBase;
import tools.gnzlz.javascript.sequelize.repositories.Repository;
import tools.gnzlz.javascript.sequelize.repositories.RepositoryValidation;
import tools.gnzlz.database.autocode.model.ACDataBase;
import tools.gnzlz.database.autocode.model.ACTable;
import tools.gnzlz.database.model.DBConfiguration;

public class Console {
    public static void main(String[] args) {

        Command command = Command.parse(args);

        if(command.type.equalsIgnoreCase("sqltite")){

        } else {
            MySQL.initConfig(command);
            Console.generate(MySQL.class, command);
        }
    }

    public static <T extends DBConfiguration> void generate(Class<T> c, Command command) {
        ACDataBase dataBase = ACDataBase.dataBase(c, command.name);
        dataBase.catalogs.forEach(catalog -> {
            Database.create(dataBase.configuration.connection().properties(), catalog, catalog.name);
            RepositoryUtils.create(catalog.name);
            if(command.jwt && command.express){
                JWTBase.create(catalog.name + "/middleware");
            }
            catalog.schemes.forEach(scheme -> {
                String schemeName = scheme.nameDefault();
                String absolute = "";
                if(command.modules){
                    absolute = "/" + catalog.name + "/" + schemeName;
                }
                if(command.express){
                    RouteSequelizeUnited.create(scheme, catalog.name, command);
                    if(command.jwt){
                        RouteSequelizeLogin.create(catalog.name + "/" + schemeName + "/route", absolute);
                        ControllerSequelizeLogin.create(catalog.name + "/" + schemeName + "/controller");
                    }
                }
                for (ACTable table: scheme.tables){
                    ModelBase.create(table, catalog, catalog.name + "/" + schemeName + "/base/model");
                    Model.create(table, catalog.name + "/" + schemeName + "/model");
                    ModelValidation.create(table, catalog.name + "/" + schemeName + "/validation/model");
                    RepositoryBase.create(table, catalog.name + "/" + schemeName + "/base/repository");
                    Repository.create(table, catalog.name + "/" + schemeName + "/repository");
                    RepositoryValidation.create(table, catalog.name + "/" + schemeName + "/validation/repository");
                    if(command.express){
                        RouteSequelizeBase.create(table, catalog.name + "/" + schemeName + "/base/route", absolute);
                        RouteSequelize.create(table, catalog.name + "/" + schemeName + "/route");
                        ControllerSequelizeBase.create(table, catalog.name + "/" + schemeName + "/base/controller");
                        ControllerSequelize.create(table, catalog.name + "/" + schemeName + "/controller");
                        ControllerSequelizeValidation.create(table, catalog.name + "/" + schemeName + "/validation/controller", command);
                    }
                }
            });
        });
    }
}
