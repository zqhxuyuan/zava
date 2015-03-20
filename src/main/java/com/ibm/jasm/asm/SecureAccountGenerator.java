package com.ibm.jasm.asm;

import org.objectweb.asm.*;

import java.io.IOException;

public class SecureAccountGenerator {

    private static AccountGeneratorClassLoader classLoader = new AccountGeneratorClassLoader();

    private static Class secureAccountClass;

    public Account generateSecureAccount() throws ClassFormatError, InstantiationException, IllegalAccessException, IOException {
        if (null == secureAccountClass) {
            ClassReader cr = new ClassReader("Account");
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);
            cr.accept(classAdapter, ClassReader.SKIP_DEBUG);
            byte[] data = cw.toByteArray();
            secureAccountClass = classLoader.defineClassFromClassFile("Account$EnhancedByASM",data);
        }
        return (Account) secureAccountClass.newInstance();
    }

    private static class AccountGeneratorClassLoader extends ClassLoader {
        public Class defineClassFromClassFile(String className, byte[] classFile) throws ClassFormatError {
            return defineClass("Account$EnhancedByASM", classFile, 0, classFile.length);
        }
    }
}