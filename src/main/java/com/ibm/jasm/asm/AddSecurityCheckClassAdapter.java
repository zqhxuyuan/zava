package com.ibm.jasm.asm;

import org.objectweb.asm.*;

class AddSecurityCheckClassAdapter extends ClassAdapter {

    String enhancedSuperName = "Account";

    public AddSecurityCheckClassAdapter(ClassVisitor cv) {
        //Responsechain 的下一个 ClassVisitor，这里我们将传入 ClassWriter，
        // 负责改写后代码的输出
        super(cv);
    }

    // 重写 visitMethod，访问到 "operation" 方法时，
    // 给出自定义 MethodVisitor，实际改写方法内容
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,exceptions);
        MethodVisitor wrappedMv = mv;
        if (mv != null) {
            // 对于 "operation" 方法
            if (name.equals("operation")) {
                // 使用自定义 MethodVisitor，实际改写方法内容
                wrappedMv = new AddSecurityCheckMethodAdapter(mv);
            } else if (name.equals("<init>")) {
                wrappedMv = new ChangeToChildConstructorMethodAdapter(mv,
                        enhancedSuperName);
            }
        }
        return wrappedMv;
    }

    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {
        String enhancedName = name + "$EnhancedByASM";  // 改变类命名
        enhancedSuperName = name; // 改变父类，这里是”Account”
        super.visit(version, access, enhancedName, signature,
                enhancedSuperName, interfaces);
    }
}

class AddSecurityCheckMethodAdapter extends MethodAdapter {
    public AddSecurityCheckMethodAdapter(MethodVisitor mv) {
        super(mv);
    }

    public void visitCode() {
        visitMethodInsn(Opcodes.INVOKESTATIC, "com/ibm/jasm/asm/SecurityChecker", "checkSecurity", "()V");
    }
}

class ChangeToChildConstructorMethodAdapter extends MethodAdapter {
    private String superClassName;

    public ChangeToChildConstructorMethodAdapter(MethodVisitor mv, String superClassName) {
        super(mv);
        this.superClassName = superClassName;
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        // 调用父类的构造函数时
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
            owner = superClassName;
        }
        super.visitMethodInsn(opcode, owner, name, desc);// 改写父类为 superClassName
    }
}