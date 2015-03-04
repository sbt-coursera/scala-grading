/* NEST (New Scala Test)
 * Copyright 2007-2015 LAMP/EPFL
 * @author Grzegorz Kossakowski
 */

package ch.epfl.lamp.instragent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ProfilerVisitor extends ClassVisitor implements Opcodes {

    private static String profilerClass =
        "ch/epfl/lamp/grading/instrumented/Profiler";

    public ProfilerVisitor(final ClassVisitor cv) {
        super(ASM4, cv);
    }

    private String className = null;

    @Override
    public void visit(int version, int access, String name, String signature,
        String superName, String[] interfaces) {

        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public MethodVisitor visitMethod(int access, String name, String desc,
        String signature, String[] exceptions) {

        // delegate the method call to the next
        // chained visitor
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (!profilerClass.equals(className)) {
            // only instrument non-abstract methods
            if((access & ACC_ABSTRACT) == 0) {
                assert(className != null);
                mv.visitLdcInsn(className);
                mv.visitLdcInsn(name);
                mv.visitLdcInsn(desc);
                mv.visitMethodInsn(INVOKESTATIC, profilerClass, "methodCalled",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
            }
        }
        return mv;
    }

}
