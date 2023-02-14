package com.jony.generator.utils;

import com.jony.generator.entity.Mode;
import com.jony.generator.invoker.base.AbstractInvoker;
import com.jony.generator.task.*;
import com.jony.generator.task.base.AbstractTask;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

/**
 * @author jony
 * @since 2023/1/1
 */
@NoArgsConstructor
public class TaskQueue {

    /**
     * 任务队列
     */
    private final LinkedList<AbstractTask> tasks = new LinkedList<>();

    /**
     * 初始化共性任务，包括Controller、ServiceImpl、Service、Dao、Mapper任务
     *
     * @param invoker 执行器
     */
    private void initCommonTasks(AbstractInvoker invoker) {
        if (!StringUtil.isEmpty(ConfigUtil.getConfiguration().getPath().getController())) {
            tasks.add(new ControllerTask(invoker));
        }
        if (!StringUtil.isEmpty(ConfigUtil.getConfiguration().getPath().getService())) {
            tasks.add(new ServiceTask(invoker));
        }
        if (!StringUtil.isEmpty(ConfigUtil.getConfiguration().getPath().getInterf())) {
            tasks.add(new InterfaceTask(invoker));
        }
        if (!StringUtil.isEmpty(ConfigUtil.getConfiguration().getPath().getDao())) {
            tasks.add(new DaoTask(invoker));
        }
        if (!StringUtil.isEmpty(ConfigUtil.getConfiguration().getPath().getMapper())) {
            tasks.add(new MapperTask(invoker));
        }
    }

    /**
     * 初始化单表生成任务，包括Entity、Mapper任务
     *
     * @param invoker 执行器
     */
    public void initSingleTasks(AbstractInvoker invoker) {
        initCommonTasks(invoker);
        if (!StringUtil.isEmpty(ConfigUtil.getConfiguration().getPath().getEntity())) {
            tasks.add(new EntityTask(Mode.ENTITY_MAIN, invoker));
        }
    }

    /**
     * 任务队列是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * 取出一个任务
     *
     * @return 任务
     */
    public AbstractTask poll() {
        return tasks.poll();
    }
}
