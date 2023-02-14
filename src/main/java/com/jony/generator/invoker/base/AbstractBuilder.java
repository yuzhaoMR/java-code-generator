package com.jony.generator.invoker.base;

import com.jony.generator.exception.ValidException;

/**
 * @author jony
 * @since 2023/1/1
 */
public abstract class AbstractBuilder {
    protected AbstractInvoker invoker;

    public Invoker build() {
        if (!isParametersValid()) {
            return null;
        }
        return invoker;
    }

    private boolean isParametersValid() {
        try {
            checkBeforeBuild();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 在创建invoker对象前检查，由子类去实现
     *
     * @throws ValidException 检查失败则抛出异常
     */
    protected abstract void checkBeforeBuild() throws ValidException;
}
