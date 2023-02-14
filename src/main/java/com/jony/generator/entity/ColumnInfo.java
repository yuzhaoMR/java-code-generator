package com.jony.generator.entity;

import com.jony.generator.utils.ConvertorUtil;
import com.jony.generator.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.sql.JDBCType;

/**
 * 数据列实体
 *
 * @author jony
 * @since 2023/1/1
 */
@Data
public class ColumnInfo implements Serializable {
    /**
     * 列名
     */
    private String columnName;
    /**
     * 数据列类型
     */
    private JDBCType columnType;
    /**
     * 列备注
     */
    private String remarks;
    /**
     * 表备注
     */
    private String tableRemarks;
    /**
     * 是否主键
     */
    private boolean isPrimaryKey;
    /**
     * 列名 -- 属性名
     */
    private String propertyName;

    /**
     * 数据列类型 -- Java类型
     */
    private String propertyType;

    public ColumnInfo(String columnName, int columnType, String remarks, String tableRemarks, boolean isPrimaryKey) {
        this.columnName = columnName;
        this.propertyName = StringUtil.columnName2PropertyName(columnName);
        this.columnType = JDBCType.valueOf(columnType);
        this.propertyType = ConvertorUtil.parseTypeFormSqlType(JDBCType.valueOf(columnType));
        this.remarks = remarks;
        this.tableRemarks = tableRemarks;
        this.isPrimaryKey = isPrimaryKey;
    }
}
