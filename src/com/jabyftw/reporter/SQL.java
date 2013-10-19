package com.jabyftw.reporter;

/**
 *
 * @author Rafael
 */
public class SQL {

    private Reporter reporter;

    public SQL(Reporter plugin) {
        this.reporter = plugin;
    }

    public String createTable = "CREATE TABLE IF NOT EXISTS `" + reporter.tableName + "` ("
            + "  `id` int(11) NOT NULL AUTO_INCREMENT,"
            + "  `sender` varchar(32) NOT NULL,"
            + "  `reported` varchar(32) NOT NULL,"
            + "  `x` int(11) NOT NULL,"
            + "  `y` int(11) NOT NULL,"
            + "  `z` int(11) NOT NULL,"
            + "  `reason` text NOT NULL,"
            + //"  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "  PRIMARY KEY (`id`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;";

    public String loadReport = "SELECT `id`, `sender`, `reported`, `x`, `y`, `z`, `reason` FROM `" + reporter.tableName + "` LIMIT " + reporter.rowLimit + ";";
    
    public String sendReport = "INSERT INTO `minecraft`.`" + reporter.tableName + "` (`sender`, `reported`, `x`, `y`, `z`, `reason`) VALUES (?, ?, ?, ?, ?, ?);";
}
