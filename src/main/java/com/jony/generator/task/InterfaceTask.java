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
public class InterfaceTask extends AbstractTask {

    public InterfaceTask(AbstractInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void run() throws IOException, TemplateException {
        // 构造Service接口填充数据
        Map<String, Object> interfaceData = new HashMap<>();
        interfaceData.put("Configuration", ConfigUtil.getConfiguration());
        interfaceData.put("ClassName", ConfigUtil.getConfiguration().getName().getEntity().replace(Constant.PLACEHOLDER,
                invoker.getClassName()));
        interfaceData.put("EntityName", StringUtil.firstToLowerCase(invoker.getClassName()));
        interfaceData.put("InterfaceClassName", ConfigUtil.getConfiguration().getName().getInterf()
                .replace(Constant.PLACEHOLDER, invoker.getClassName()));
        String filePath = FileUtil.getSourcePath() + StringUtil.package2Path(ConfigUtil.getConfiguration().getPackageName())
                + StringUtil.package2Path(ConfigUtil.getConfiguration().getPath().getInterf());
        String fileName = ConfigUtil.getConfiguration().getName().getInterf().replace(Constant.PLACEHOLDER, invoker.getClassName()) + ".java";
        // 生成Service接口文件
        FileUtil.generateToJava(FreemarkerConfigUtil.TYPE_INTERFACE, interfaceData, filePath, fileName);
    }
}
