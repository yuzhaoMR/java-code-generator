package com.jony.generator.task;

import com.jony.generator.entity.Constant;
import com.jony.generator.invoker.base.AbstractInvoker;
import com.jony.generator.task.base.AbstractTask;
import com.jony.generator.utils.*;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jony
 * @since 2023/1/1
 */
public class DaoTask extends AbstractTask {

    public DaoTask(AbstractInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void run() throws IOException, TemplateException {
        // 构造Dao填充数据
        Map<String, Object> daoData = new HashMap<>();
        daoData.put("Configuration", ConfigUtil.getConfiguration());
        daoData.put("ClassName", ConfigUtil.getConfiguration().getName().getEntity().replace(Constant.PLACEHOLDER, invoker.getClassName()));
        daoData.put("EntityName", StringUtil.firstToLowerCase(invoker.getClassName()));
        daoData.put("DaoClassName", ConfigUtil.getConfiguration().getName().getDao().replace(Constant.PLACEHOLDER, invoker.getClassName()));
        String filePath = FileUtil.getSourcePath() + StringUtil.package2Path(ConfigUtil.getConfiguration().getPackageName())
                + StringUtil.package2Path(ConfigUtil.getConfiguration().getPath().getDao());
        String fileName = ConfigUtil.getConfiguration().getName().getDao().replace(Constant.PLACEHOLDER, invoker.getClassName()) + ".java";
        // 生成dao文件
        FileUtil.generateToJava(FreemarkerConfigUtil.TYPE_DAO, daoData, filePath, fileName);
    }
}
