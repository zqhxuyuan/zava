package com.ibm.jasm.asm;

import org.objectweb.asm.*;

/**
 * Created by zqhxuyuan on 15-3-19.
 */
public class VistorChain {

    public static void main(String[] args) {

    }

    public void testVisitorChain() throws Exception{
        ClassWriter  classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassAdapter delLoginClassAdaptor = new DelLoginClassAdapter(classWriter);
        ClassAdapter accessClassAdaptor = new AccessClassAdapter(delLoginClassAdaptor);

        // 增加新的类、方法、字段
        String strFileName = "/home/hadoop/test.class";
        ClassReader classReader = new ClassReader(strFileName);
        classReader.accept(accessClassAdaptor, ClassReader.SKIP_DEBUG);
    }
}

// 删除类的字段、方法、指令：只需在职责链传递过程中中断委派，不访问相应的 visit 方法即可，
// 比如删除方法时只需直接返回 null，而不是返回由 visitMethod方法返回的 MethodVisitor对象。
class DelLoginClassAdapter extends ClassAdapter {
    public DelLoginClassAdapter(ClassVisitor cv) {
        super(cv);
    }

    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        if (name.equals("login")) {
            return null;
        }
        return cv.visitMethod(access, name, desc, signature, exceptions);
    }
}

// 修改类、字段、方法的名字或修饰符：在职责链传递过程中替换调用参数
class AccessClassAdapter extends ClassAdapter {
    public AccessClassAdapter(ClassVisitor cv) {
        super(cv);
    }

    public FieldVisitor visitField(final int access, final String name,
                                   final String desc, final String signature, final Object value) {
        int privateAccess = Opcodes.ACC_PRIVATE;
        return cv.visitField(privateAccess, name, desc, signature, value);
    }
}
