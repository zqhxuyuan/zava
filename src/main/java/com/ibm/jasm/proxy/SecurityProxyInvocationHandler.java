package com.ibm.jasm.proxy;

import com.ibm.jasm.decorator.Account;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SecurityProxyInvocationHandler implements InvocationHandler {

    private Object proxyedObject;

    public SecurityProxyInvocationHandler(Object o) {
        proxyedObject = o;
    }

    public Object invoke(Object object, Method method, Object[] arguments) throws Throwable {
        if (object instanceof Account && method.getName().equals("opertaion")) {
            SecurityChecker.checkSecurity();
        }
        return method.invoke(proxyedObject, arguments);
    }
}