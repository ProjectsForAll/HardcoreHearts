package host.plas.hardcorehearts.sql;

import host.plas.bou.sql.DBOperator;
import host.plas.bou.sql.DatabaseType;
import lombok.NonNull;

public class Statements {
    public enum MySQL {
        CREATE_DATABASE("CREATE DATABASE IF NOT EXISTS `%database_Name%`;"),
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS `%table_prefix%users` ("
                + "`Uuid` VARCHAR(36) NOT NULL, "
                + "`Name` VARCHAR(16) NOT NULL, "
                + "`Lives` INT NOT NULL, "
                + "`Deaths` INT NOT NULL, "
                + "`Kills` INT NOT NULL, "
                + "`Revives` INT NOT NULL, "
                + "`IsDeathBanned` BIT NOT NULL DEFAULT 0, "
                + "PRIMARY KEY (`Uuid`));"),
        UPDATE("INSERT INTO `%table_prefix%users` (`Uuid`, `Name`, `Lives`, `Deaths`, `Kills`, `Revives`, `IsDeathBanned`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `Name` = ?, `Lives` = ?, `Deaths` = ?, `Kills` = ?, `Revives` = ?, `IsDeathBanned` = ?;"),
        SELECT("SELECT * FROM `%table_prefix%users` WHERE `Uuid` = ?;"),
        DELETE("DELETE FROM `%table_prefix%users` WHERE `Uuid` = ?;");

        private final String statement;

        MySQL(String statement) {
            this.statement = statement;
        }

        public String getStatement() {
            return statement;
        }
        
        @NonNull
        public static String getStatementHard(Centralized statement) {
            switch (statement) {
                case CREATE_DATABASE:
                    return CREATE_DATABASE.getStatement();
                case CREATE_TABLE:
                    return CREATE_TABLE.getStatement();
                case UPDATE:
                    return UPDATE.getStatement();
                case SELECT:
                    return SELECT.getStatement();
                case DELETE:
                    return DELETE.getStatement();
                default:
                    return "";
            }
        }
    }
    
    public enum SQLite {
        CREATE_DATABASE(""),
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS `%table_prefix%users` ("
                + "`Uuid` VARCHAR(36) NOT NULL, "
                + "`Name` VARCHAR(16) NOT NULL, "
                + "`Lives` INT NOT NULL, "
                + "`Deaths` INT NOT NULL, "
                + "`Kills` INT NOT NULL, "
                + "`Revives` INT NOT NULL, "
                + "`IsDeathBanned` BIT NOT NULL DEFAULT 0, "
                + "PRIMARY KEY (`Uuid`));"),
        UPDATE("INSERT OR REPLACE INTO `%table_prefix%users` (`Uuid`, `Name`, `Lives`, `Deaths`, `Kills`, `Revives`, `IsDeathBanned`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);"),
        SELECT("SELECT * FROM `%table_prefix%users` WHERE `Uuid` = ?;"),
        DELETE("DELETE FROM `%table_prefix%users` WHERE `Uuid` = ?;");

        private final String statement;

        SQLite(String statement) {
            this.statement = statement;
        }

        public String getStatement() {
            return statement;
        }
        
        @NonNull
        public static String getStatementHard(Centralized statement) {
            switch (statement) {
                case CREATE_DATABASE:
                    return CREATE_DATABASE.getStatement();
                case CREATE_TABLE:
                    return CREATE_TABLE.getStatement();
                case UPDATE:
                    return UPDATE.getStatement();
                case SELECT:
                    return SELECT.getStatement();
                case DELETE:
                    return DELETE.getStatement();
                default:
                    return "";
            }
        }
    }
    
    public enum Centralized {
        CREATE_DATABASE,
        CREATE_TABLE,
        UPDATE,
        SELECT,
        DELETE
        ;
    }
    
    public static String getStatement(DBOperator operator, Centralized statement) {
        if (operator.getType() == DatabaseType.MYSQL) {
            return MySQL.getStatementHard(statement)
                    .replace("%table_prefix%", operator.getConnectorSet().getTablePrefix())
                    .replace("%database_Name%", operator.getConnectorSet().getDatabase());
        } else if (operator.getType() == DatabaseType.SQLITE) {
            return SQLite.getStatementHard(statement)
                    .replace("%table_prefix%", operator.getConnectorSet().getTablePrefix())
                    .replace("%database_Name%", operator.getConnectorSet().getDatabase());
        } else {
            return "";
        }
    }
}
