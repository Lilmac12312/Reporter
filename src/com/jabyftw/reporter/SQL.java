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
            + "  `resolved` tinyint(1) NOT NULL DEFAULT '0',"
            + "  PRIMARY KEY (`id`)"
            + ") ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;";

    public String loadReport = "SELECT `id`, `sender`, `reported`, `x`, `y`, `z`, `reason` FROM `" + reporter.tableName + "` WHERE `resolved`=0 LIMIT " + reporter.rowLimit + ";";

    public String sendReport = "INSERT INTO `" + reporter.tableName + "` (`sender`, `reported`, `x`, `y`, `z`, `reason`, `resolved`) VALUES (?, ?, ?, ?, ?, ?, ?);";

    public String updateStatus = "UPDATE `reporter` SET `resolved`=? WHERE `id`=?";
}
