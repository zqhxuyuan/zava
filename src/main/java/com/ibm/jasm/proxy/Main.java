package com.ibm.jasm.proxy;

import com.ibm.jasm.decorator.Account;
import com.ibm.jasm.decorator.AccountImpl;

import java.lang.reflect.Proxy;

/**
 * Created by zqhxuyuan on 15-3-19.
 */
public class Main {

    public static void main(String[] args) {
        Account account = (Account) Proxy.newProxyInstance(
                Account.class.getClassLoader(),
                new Class[]{Account.class},
                new SecurityProxyInvocationHandler(new AccountImpl())
        );
        account.operation();
    }
}
