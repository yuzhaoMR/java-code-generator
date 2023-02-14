package com.jony.generator.invoker.base;

import com.jony.generator.db.ConnectionUtil;
import com.jony.generator.entity.ColumnInfo;
import com.jony.generator.exception.CommonException;
import com.jony.generator.task.base.AbstractTask;
import com.jony.generator.utils.TaskQueue;
import freemarker.template.TemplateException;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author jony
 * @since 2023/1/1
 */
@Data
public abstract class AbstractInvoker implements Invoker, Serializable {
    private static final long serialVersionUID = 2405172041950251807L;

    /**
     * 主表名
     */
    protected String tableName;
    /**
     * 主类名
     */
    protected String className;
    /**
     * 主表元数据
     */
    protected List<ColumnInfo> tableInfos;
    /**
     * 数据库连接工具
     */
    protected ConnectionUtil connectionUtil = new ConnectionUtil();
    /**
     * 任务队列
     */
    protected TaskQueue taskQueue = new TaskQueue();

    /**
     * 获取表元数据，模板方法，由子类实现
     *
     * @throws CommonException 获取元数据失败则抛出异常
     */
    protected abstract void queryMetaData() throws CommonException;

    /**
     * 初始化代码生成任务，模板方法，由子类实现
     */
    protected abstract void initTasks();

    /**
     * 开始生成代码
     */
    @Override
    public void execute() {
        try {
            var executorPool = new ThreadPoolExecutor(6, 6, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(10));
            queryMetaData();
            initTasks();
            while (!taskQueue.isEmpty()) {
                AbstractTask task = taskQueue.poll();
                executorPool.execute(() -> {
                    try {
                        task.run();
                    } catch (IOException | TemplateException e) {
                        e.printStackTrace();
                    }
                });
            }
            executorPool.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
