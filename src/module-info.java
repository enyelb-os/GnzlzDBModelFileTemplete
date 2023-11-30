module tools.gnzlz.filetemplate {
    requires transitive tools.gnzlz.command;
    requires transitive tools.gnzlz.template;
    requires transitive tools.gnzlz.database;
    requires tools.gnzlz.system.io;

    exports tools.gnzlz.filetemplete;
    exports tools.gnzlz.filetemplete.properties;
}