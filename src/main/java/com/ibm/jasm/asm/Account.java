package com.ibm.jasm.asm;

/**
 * 与 proxy 编程不同，ASM 不需要将 Account声明成接口
 */
public class Account {

    public void operation() {
        System.out.println("operation...");
        //TODO real operation
    }

}
