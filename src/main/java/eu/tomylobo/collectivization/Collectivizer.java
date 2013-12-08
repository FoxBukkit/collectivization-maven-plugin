package eu.tomylobo.collectivization;

import org.apache.maven.plugin.logging.Log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class Collectivizer extends ClassVisitor {
    public Collectivizer(ClassVisitor cv, Log log) {
        super(Opcodes.ASM4, cv);
    }

    public FieldVisitor visitField(int access, final String name, final String desc, final String signature, final Object value) {
        if ((access & Opcodes.ACC_PUBLIC) == 0) {
            access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
            access |= Opcodes.ACC_PUBLIC;
        }

        return super.visitField(access, name, desc, signature, value);
    }
}
