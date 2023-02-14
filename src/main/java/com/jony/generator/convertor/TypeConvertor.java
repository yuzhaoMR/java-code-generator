package com.jony.generator.convertor;

import java.sql.JDBCType;

/**
 * @author jony
 * @since 2023/1/1
 */
public interface TypeConvertor {

    /**
     * 将JDBC类型转换为Java类型
     *
     * @param type JDBC类型名
     * @return Java类型名
     */
    String convertType(JDBCType type);
}
