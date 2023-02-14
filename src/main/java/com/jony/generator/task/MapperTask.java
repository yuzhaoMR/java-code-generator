package com.jony.generator.task;

import com.jony.generator.entity.ColumnInfo;
import com.jony.generator.entity.Constant;
import com.jony.generator.invoker.base.AbstractInvoker;
import com.jony.generator.task.base.AbstractTask;
import com.jony.generator.utils.*;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author jony
 * @since 2023/1/1
 */
public class MapperTask extends AbstractTask {

    public MapperTask(AbstractInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void run() throws IOException, TemplateException {
        // 构造Mapper填充数据
        Map<String, Object> mapperData = new HashMap<>();
        mapperData.put("Configuration", ConfigUtil.getConfiguration());
        mapperData.put("ClassName", ConfigUtil.getConfiguration().getName().getEntity()
                .replace(Constant.PLACEHOLDER, invoker.getClassName()));
        mapperData.put("EntityName", StringUtil.firstToLowerCase(invoker.getClassName()));
        mapperData.put("DaoClassName", ConfigUtil.getConfiguration().getName().getDao()
                .replace(Constant.PLACEHOLDER, invoker.getClassName()));
        mapperData.put("TableName", invoker.getTableName());
        mapperData.put("PrimaryKey", Objects.requireNonNull(getPrimaryKeyColumnInfo(invoker.getTableInfos())).getColumnName());
        mapperData.put("WhereId", "#{" + Objects.requireNonNull(getPrimaryKeyColumnInfo(invoker.getTableInfos())).getPropertyName() + "}");
        mapperData.put("PrimaryColumn", getPrimaryKeyColumnInfo(invoker.getTableInfos()));
        mapperData.put("InsertProperties", insertProperties());
        mapperData.put("ColumnMap", columnMap());
        mapperData.put("ResultMap", resultMap());
        mapperData.put("InsertBatchValues", insertBatchValues());
        mapperData.put("InsertValues", insertValues());
        mapperData.put("UpdateProperties", updateProperties());
        mapperData.put("Joins", "");
        mapperData.put("Association", "");
        mapperData.put("Collection", "");
        String filePath;
        if (ConfigUtil.getConfiguration().isMapperUnderSource()) {
            // mapper-under-source = true，表示将Mapper映射文件放在源文件目录下
            filePath = FileUtil.getSourcePath() + StringUtil.package2Path(ConfigUtil.getConfiguration().getPackageName())
                    + StringUtil.package2Path(ConfigUtil.getConfiguration().getPath().getMapper());
        } else {
            // 默认情况下，将Mapper映射文件放在resources下
            filePath = FileUtil.getResourcePath() + StringUtil.package2Path(ConfigUtil.getConfiguration().getPath().getMapper());
        }
        String fileName = ConfigUtil.getConfiguration().getName().getMapper().replace(Constant.PLACEHOLDER, invoker.getClassName()) + ".xml";
        // 生成Mapper文件
        FileUtil.generateToJava(FreemarkerConfigUtil.TYPE_MAPPER, mapperData, filePath, fileName);
    }

    /**
     * 获取主键列
     */
    private ColumnInfo getPrimaryKeyColumnInfo(List<ColumnInfo> list) {
        for (ColumnInfo columnInfo : list) {
            if (columnInfo.isPrimaryKey()) {
                return columnInfo;
            }
        }
        return null;
    }

    /**
     * 生成columnMap
     *
     * @return ColumnMap代码段
     */
    public String columnMap() {
        StringBuilder sb = new StringBuilder();
        invoker.getTableInfos().forEach(ForEachUtil.withIndex((info, index) -> {
            sb.append(index == 0 ? "" : Constant.SPACE_8);
            sb.append(String.format("`%s`.`%s`,\n", invoker.getTableName(), info.getColumnName()));
        }));
        return sb.substring(0, sb.toString().length() - 2);
    }


    /**
     * 生成resultMap
     *
     * @return ResultMap代码段
     */
    public String resultMap() {
        StringBuilder sb = new StringBuilder();
        invoker.getTableInfos().forEach(ForEachUtil.withIndex((info, index) -> {
            sb.append(index == 0 ? "" : Constant.SPACE_8);
            if (info.isPrimaryKey()) {
                sb.append(String.format("<id column=\"%s\" property=\"%s\" />\n", info.getColumnName(), info.getPropertyName()));
            } else {
                sb.append(String.format("<result column=\"%s\" property=\"%s\" />\n", info.getColumnName(), info.getPropertyName()));
            }
        }));
        return sb.toString();
    }

    /**
     * 生成insertProperties
     *
     * @return insertProperties代码段
     */
    public String insertProperties() {
        StringBuilder sb = new StringBuilder();
        invoker.getTableInfos().forEach(ForEachUtil.withIndex((info, index) -> {
            sb.append(index == 0 ? "" : Constant.SPACE_12);
            sb.append(String.format("`%s`,\n", info.getColumnName()));
        }));
        return sb.toString().substring(0, sb.toString().length() - 2);
    }

    /**
     * 生成insertValues
     *
     * @return insertValues代码段
     */
    public String insertValues() {
        StringBuilder sb = new StringBuilder();
        invoker.getTableInfos().forEach(ForEachUtil.withIndex((info, index) -> {
            sb.append(index == 0 ? "" : Constant.SPACE_12);
            sb.append(String.format("#{%s},\n", info.getPropertyName()));
        }));
        return sb.substring(0, sb.toString().length() - 2);
    }

    /**
     * 生成insertBatchValues
     *
     * @return insertBatchValues代码段
     */
    public String insertBatchValues() {
        StringBuilder sb = new StringBuilder();
        invoker.getTableInfos().forEach(ForEachUtil.withIndex((info, index) -> {
            sb.append(index == 0 ? "" : Constant.SPACE_12);
            sb.append(String.format("#{%s.%s},\n", StringUtil.firstToLowerCase(invoker.getClassName()), info.getPropertyName()));
        }));
        return sb.substring(0, sb.toString().length() - 2);
    }

    /**
     * 生成updateProperties
     *
     * @return updateProperties代码段
     */
    public String updateProperties() {
        StringBuilder sb = new StringBuilder();
        invoker.getTableInfos().forEach(ForEachUtil.withIndex((info, index) -> {
            sb.append(index == 0 ? "" : Constant.SPACE_8);
            sb.append(String.format("`%s` = #{%s},\n", info.getColumnName(), info.getPropertyName()));
        }));
        return sb.substring(0, sb.toString().length() - 2);
    }
}
