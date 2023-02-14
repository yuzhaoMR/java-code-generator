package com.jony.generator.utils;

import com.jony.generator.convertor.DefaultConvertor;
import com.jony.generator.convertor.TypeConvertor;
import lombok.NoArgsConstructor;

import java.sql.JDBCType;

import static lombok.AccessLevel.PRIVATE;

/**
 * 类型转换器工具类
 *
 * @author jony
 * @since 2021/1/1
 */
@NoArgsConstructor(access = PRIVATE)
public class ConvertorUtil {
    /**
     * 类型转换器
     */
    private static volatile TypeConvertor convertor;

    /**
     * 将数据库数据类型转换为Java数据类型
     *
     * @param type jdbc类型
     * @return java类型
     */
    public static String parseTypeFormSqlType(JDBCType type) {
        /*
         * 用户配置了错误的TypeConvertor会导致convertor为null
         * 在生成多表关系代码时，会有两个EntityTask并发执行，防止创建多个实例，采用double-check的单例模式
         */
        if (convertor == null) {
            synchronized (ConvertorUtil.class) {
                if (convertor == null) {
                    convertor = newInstance();
                }
            }
        }
        return convertor.convertType(type);
    }

    private static TypeConvertor newInstance() {
        TypeConvertor convertor;
        String convertorClass = ConfigUtil.getConfiguration().getConvertor();
        // 用户未配置类型转换器，使用默认转换器
        if (StringUtil.isEmpty(convertorClass)) {
            convertor = new DefaultConvertor();
        } else {
            // 加载用户定义的类型转换器
            try {
                var clazz = Class.forName(ConfigUtil.getConfiguration().getConvertor());
                convertor = (TypeConvertor) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.err.println(String.format("Can not find %s, DefaultConvertor will be used.", convertorClass));
                convertor = new DefaultConvertor();
            }
        }
        return convertor;
    }

}
