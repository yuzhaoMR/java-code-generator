package com.jony.generator.invoker;

import com.jony.generator.exception.CommonException;
import com.jony.generator.exception.ValidException;
import com.jony.generator.invoker.base.AbstractBuilder;
import com.jony.generator.invoker.base.AbstractInvoker;
import com.jony.generator.utils.StringUtil;
import lombok.*;

/**
 * @author jony
 * @since 2023/1/1
 */
@AllArgsConstructor
public class SingleInvoker extends AbstractInvoker {

    @Override
    protected void queryMetaData() throws CommonException {
        tableInfos = connectionUtil.getMetaData(tableName);
    }

    @Override
    protected void initTasks() {
        taskQueue.initSingleTasks(this);
    }

    public static class Builder extends AbstractBuilder {
        public Builder() {
            invoker = new SingleInvoker();
        }

        public Builder setTableName(String tableName) {
            invoker.setTableName(tableName);
            return this;
        }

        public Builder setClassName(String className) {
            invoker.setClassName(className);
            return this;
        }

        @Override
        public void checkBeforeBuild() throws ValidException {
            if (StringUtil.isEmpty(invoker.getTableName())) {
                throw new ValidException("Table name can't be null.");
            }
            if (StringUtil.isEmpty(invoker.getClassName())) {
                invoker.setClassName(StringUtil.tableName2ClassName(invoker.getTableName()));
            }
        }
    }
}
