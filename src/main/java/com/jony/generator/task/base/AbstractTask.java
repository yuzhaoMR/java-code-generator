package com.jony.generator.task.base;

import com.jony.generator.invoker.base.AbstractInvoker;
import freemarker.template.TemplateException;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author jony
 * @since 2023/1/1
 */
@NoArgsConstructor
public abstract class AbstractTask implements Serializable {
    private static final long serialVersionUID = 2405172041950251807L;
    protected AbstractInvoker invoker;

    /**
     * 执行任务
     *
     * @throws IOException       文件读写异常
     * @throws TemplateException 模板异常
     */
    public abstract void run() throws IOException, TemplateException;

}
