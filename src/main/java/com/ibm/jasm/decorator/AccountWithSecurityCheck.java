package com.ibm.jasm.decorator;

public class AccountWithSecurityCheck implements Account {
    private  Account account;
    public AccountWithSecurityCheck (Account account) {
        this.account = account;
    }
    public void operation() {
        SecurityChecker.checkSecurity();
        account.operation();
    }
}