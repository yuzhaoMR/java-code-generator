package com.jony.generator.application;

import com.jony.generator.invoker.SingleInvoker;

/**
 * @author jony
 * @since 2023/1/1
 */
public class Main {

    public static void main(String[] args) {
        generateSingleTableData();
    }

    public static void generateSingleTableData() {
        var invoker = new SingleInvoker.Builder()
                .setTableName("pipe_debit_note")
                .setClassName("DebitNote")
                .build();

        invoker.execute();
    }
}
