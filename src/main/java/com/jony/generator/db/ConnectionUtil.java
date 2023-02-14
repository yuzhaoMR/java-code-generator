package com.jony.generator.db;

import com.jony.generator.entity.ColumnInfo;
import com.jony.generator.exception.CommonException;
import com.jony.generator.utils.ConfigUtil;
import com.jony.generator.utils.StringUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * 数据库连接工具类
 *
 * @author jony
 * @since 2023/1/1
 */
public class ConnectionUtil {
    /**
     * 数据库连接
     */
    private Connection connection;

    /**
     * 初始化数据库连接
     *
     * @return 连接是否建立成功
     */
    public boolean initConnection() {
        try {
            Class.forName(DataBaseFactory.getDriver(ConfigUtil.getConfiguration().getDb().getUrl()));
            String url = ConfigUtil.getConfiguration().getDb().getUrl();
            String username = ConfigUtil.getConfiguration().getDb().getUsername();
            String password = ConfigUtil.getConfiguration().getDb().getPassword();
            var properties = new Properties();
            properties.put("user", username);
            properties.put("password", password == null ? "" : password);
            properties.setProperty("remarks", "true");
            properties.setProperty("useInformationSchema", "true");
            properties.setProperty("nullCatalogMeansCurrent", "true");
            connection = DriverManager.getConnection(url, properties);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取表结构数据
     *
     * @param tableName 表名
     * @return 包含表结构数据的列表
     * @throws CommonException CommonException
     */
    public List<ColumnInfo> getMetaData(String tableName) throws CommonException {
        if (!initConnection()) {
            throw new CommonException("Failed to connect to database at url: " + ConfigUtil.getConfiguration().getDb().getUrl());
        }
        // 获取主键
        String primaryKey = getPrimaryKey(tableName);
        // 获取表注释
        String tableRemark = getTableRemark(tableName);
        // 获取列信息
        List<ColumnInfo> columnInfos = getColumnInfos(tableName, primaryKey, tableRemark);
        closeConnection();
        return columnInfos;
    }

    /**
     * 获取主键
     *
     * @param tableName 表名
     * @return 主键名称
     */
    private String getPrimaryKey(String tableName) {
        try {
            var keyResultSet = connection.getMetaData().getPrimaryKeys(DataBaseFactory.getCatalog(connection),
                    DataBaseFactory.getSchema(connection), tableName);
            String primaryKey = null;
            if (keyResultSet.next()) {
                primaryKey = keyResultSet.getObject(4).toString();
            }
            keyResultSet.close();
            return primaryKey;
        } catch (SQLException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取表注释
     *
     * @param tableName 表名
     * @return 表注释
     */
    private String getTableRemark(String tableName) {
        try {
            String tableRemark = null;
            if (connection.getMetaData().getURL().contains("sqlserver")) {
                // SQLServer
                tableRemark = parseSqlServerTableRemarks(tableName);
            } else {
                // Oracle & MySQL
                var tableResultSet = connection.getMetaData().getTables(DataBaseFactory.getCatalog(connection),
                        DataBaseFactory.getSchema(connection), tableName, new String[]{"TABLE"});
                if (tableResultSet.next()) {
                    tableRemark = StringUtil.isEmpty(tableResultSet.getString("REMARKS")) ?
                            "Unknown Table" : tableResultSet.getString("REMARKS");
                }
                tableResultSet.close();
            }
            return tableRemark;
        } catch (SQLException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取列信息
     *
     * @param tableName   表名
     * @param primaryKey  主键列名
     * @param tableRemark 表注释
     * @return 列信息
     */
    private List<ColumnInfo> getColumnInfos(String tableName, String primaryKey, String tableRemark) {
        try {
            List<ColumnInfo> columnInfos = new ArrayList<>();
            var columnResultSet = connection.getMetaData().getColumns(DataBaseFactory.getCatalog(connection),
                    DataBaseFactory.getSchema(connection), tableName, "%");
            while (columnResultSet.next()) {
                boolean isPrimaryKey;
                isPrimaryKey = columnResultSet.getString("COLUMN_NAME").equals(primaryKey);
                var info = new ColumnInfo(columnResultSet.getString("COLUMN_NAME"), columnResultSet.getInt("DATA_TYPE"),
                        StringUtil.isEmpty(columnResultSet.getString("REMARKS")) ? "Unknown" : columnResultSet.getString("REMARKS"),
                        tableRemark, isPrimaryKey);
                columnInfos.add(info);
            }
            columnResultSet.close();
            if (columnInfos.isEmpty()) {
                closeConnection();
                throw new CommonException("Can not find column information from table:" + tableName);
            }
            // SQLServer需要单独处理列REMARKS
            if (connection.getMetaData().getURL().contains("sqlserver")) {
                parseSqlServerColumnRemarks(tableName, columnInfos);
            }
            return columnInfos;
        } catch (SQLException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 主动查询SqlServer指定表的注释
     *
     * @param tableName 表名
     * @return 表注释
     * @throws SQLException SQLException
     */
    private String parseSqlServerTableRemarks(String tableName) throws SQLException {
        try {
            String tableRemarks = null;
            String sql = "SELECT CAST(ISNULL(p.value, '') AS nvarchar(25)) AS REMARKS FROM sys.tables t " +
                    "LEFT JOIN sys.extended_properties p ON p.major_id=t.object_id AND p.minor_id=0 AND p.class=1 " +
                    "WHERE t.name = ?";
            var preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, tableName);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tableRemarks = StringUtil.isEmpty(resultSet.getString("REMARKS")) ? "Unknown Table" : resultSet.getString("REMARKS");
            }
            resultSet.close();
            preparedStatement.close();
            return tableRemarks;
        } catch (SQLException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 主动查询SqlServer指定表的数据列的注释
     *
     * @param tableName 表名
     * @throws SQLException SQLException
     */
    private void parseSqlServerColumnRemarks(String tableName, List<ColumnInfo> columnInfos) throws SQLException {
        HashMap<String, String> map = new HashMap<>();
        String sql = "SELECT c.name AS COLUMN_NAME, CAST(ISNULL(p.value, '') AS nvarchar(25)) AS REMARKS " +
                "FROM sys.tables t " +
                "INNER JOIN sys.columns c ON c.object_id = t.object_id " +
                "LEFT JOIN sys.extended_properties p ON p.major_id = c.object_id AND p.minor_id = c.column_id " +
                "WHERE t.name = ?";
        var preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, tableName);
        var resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            map.put(resultSet.getString("COLUMN_NAME"), StringUtil.isEmpty(resultSet.getString("REMARKS")) ?
                    "Unknown" : resultSet.getString("REMARKS"));
        }
        for (ColumnInfo columnInfo : columnInfos) {
            columnInfo.setRemarks(map.get(columnInfo.getColumnName()));
        }
        resultSet.close();
        preparedStatement.close();
    }

    /**
     * 关闭数据库连接
     *
     * @throws CommonException CommonException
     */
    private void closeConnection() throws CommonException {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new CommonException(e.getMessage());
        }
    }
}
