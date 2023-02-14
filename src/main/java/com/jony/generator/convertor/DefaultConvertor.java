package com.jony.generator.convertor;

import java.sql.JDBCType;

/**
 * @author jony
 * @since 2023/1/1
 */
public class DefaultConvertor implements TypeConvertor {

    /**
     * 将JDBC类型转换为Java类型
     *
     * @param type JDBC类型名
     */
    @Override
    public String convertType(JDBCType type) {
        var stringBuilder = new StringBuilder();
        switch (type) {
            case BIT:
            case BOOLEAN:
                stringBuilder.append("Boolean");
                break;
            case TINYINT:
            case SMALLINT:
            case INTEGER:
                stringBuilder.append("Integer");
                break;
            case BIGINT:
                stringBuilder.append("Long");
                break;
            case REAL:
                stringBuilder.append("Float");
                break;
            case FLOAT:
            case DOUBLE:
                stringBuilder.append("Double");
                break;
            case DECIMAL:
            case NUMERIC:
                stringBuilder.append("BigDecimal");
                break;
            case VARCHAR:
            case CHAR:
            case NCHAR:
            case NVARCHAR:
            case LONGVARCHAR:
            case LONGNVARCHAR:
                stringBuilder.append("String");
                break;
            case DATE:
            case TIME:
            case TIMESTAMP:
                stringBuilder.append("Date");
                break;
            case CLOB:
            case NCLOB:
            case BLOB:
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
                stringBuilder.append("byte[]");
                break;
            default:
                stringBuilder.append("Object");
        }
        return stringBuilder.toString();
    }
}
