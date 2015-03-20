package com.ibm.jasm.asm;

import java.io.File;
import java.io.FileOutputStream;
import org.objectweb.asm.*;

public class Generator{
    public static void main(String[] args) throws Exception {
        ClassReader cr = new ClassReader("com.ibm.jasm.asm.Account");
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);
        cr.accept(classAdapter, ClassReader.SKIP_DEBUG);

        byte[] data = cw.toByteArray();
        //覆盖Account.class文件. 注意要和工具生成的位置一样,否则执行Main方法时,工具没办法调用到.class文件
        File file = new File("/home/hadoop/IdeaProjects/go-bigdata/zava/target/classes/com/ibm/jasm/asm/Account.class");
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(data);
        fout.close();
    }
}