package com.jony.generator.entity;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * 常量
 *
 * @author jony
 * @since 2023/1/1
 */
@NoArgsConstructor(access = PRIVATE)
public class Constant {
    /**
     * 空格 * 4
     */
    public static final String SPACE_4 = "    ";
    /**
     * 空格 * 8
     */
    public static final String SPACE_8 = "        ";
    /**
     * 空格 * 12
     */
    public static final String SPACE_12 = "            ";
    /**
     * 文件名占位符
     */
    public static final String PLACEHOLDER = "$s";
}
