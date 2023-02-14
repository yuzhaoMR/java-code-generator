package com.jony.generator.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库驱动工厂类
 *
 * @author jony
 * @since 2023/1/1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataBaseFactory {
    private static final String DRIVER_MYSQL_5 = "com.mysql.jdbc.Driver";
    private static final String DRIVER_MYSQL_UP = "com.mysql.cj.jdbc.Driver";
    private static final String DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";
    private static final String DRIVER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /**
     * 根据数据库连接url获取数据库驱动
     *
     * @param url 数据库url
     * @return 驱动类名
     */
    public static String getDriver(String url) {
        if (url.contains("mysql")) {
            if (url.contains("serverTimezone")) {
                return DRIVER_MYSQL_UP;
            } else {
                return DRIVER_MYSQL_5;
            }
        }
        if (url.contains("oracle")) {
            return DRIVER_ORACLE;
        }
        if (url.contains("sqlserver")) {
            return DRIVER_SQLSERVER;
        }
        return null;
    }

    /**
     * 获取catalog
     *
     * @param connection 数据库连接
     * @return catalog
     * @throws SQLException SQLException
     */
    public static String getCatalog(Connection connection) throws SQLException {
        String url = connection.getMetaData().getURL();
        if (url.contains("mysql")) {
            return null;
        } else if (url.contains("oracle")) {
            return null;
        } else if (url.contains("sqlserver")) {
            return url.substring(url.lastIndexOf("=") + 1);
        }
        return null;
    }

    /**
     * 获取schema
     *
     * @param connection 数据库连接
     * @return schema
     * @throws SQLException SQLException
     */
    public static String getSchema(Connection connection) throws SQLException {
        String url = connection.getMetaData().getURL();
        if (url.contains("mysql")) {
            if (url.contains("?")) {
                url = url.replace("jdbc:mysql://", "");
                return url.substring(url.indexOf("/") + 1, url.lastIndexOf("?"));
            } else {
                return url.substring(url.lastIndexOf("/") + 1);
            }
        } else if (url.contains("oracle")) {
            return connection.getMetaData().getUserName();
        } else if (url.contains("sqlserver")) {
            return connection.getSchema();
        }
        return null;
    }

}
