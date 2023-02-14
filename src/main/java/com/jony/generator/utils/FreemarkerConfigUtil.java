package com.jony.generator.utils;

import freemarker.template.Configuration;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author jony
 * @since 2021/1/1
 */
@NoArgsConstructor(access = PRIVATE)
public class FreemarkerConfigUtil {
    /**
     * 模板路径
     */
    private static final String PATH = new File(Objects.requireNonNull(FreemarkerConfigUtil.class.getClassLoader().getResource("ftls")).getFile()).getPath();
    /**
     * freemarker配置
     */
    private static Configuration configuration;
    public static final int TYPE_ENTITY = 0;
    public static final int TYPE_DAO = 1;
    public static final int TYPE_SERVICE = 2;
    public static final int TYPE_CONTROLLER = 3;
    public static final int TYPE_MAPPER = 4;
    public static final int TYPE_INTERFACE = 5;

    public static synchronized Configuration getInstance() {
        if (configuration == null) {
            configuration = new Configuration(Configuration.VERSION_2_3_23);
            try {
                if (PATH.contains("jar")) {
                    configuration.setClassForTemplateLoading(FreemarkerConfigUtil.class, "/ftls");
                } else {
                    configuration.setDirectoryForTemplateLoading(new File(PATH));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            configuration.setEncoding(Locale.CHINA, "utf-8");
        }

        return configuration;
    }
}
