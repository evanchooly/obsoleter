package com.antwerkz.maven;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ASM9;

public final class Obsoleter {
    private static final String OBSOLETE_DESCRIPTOR = Obsolete.class.descriptorString();

    public static void rewriteFile(Path classFile) throws IOException {
        ClassReader reader;
        try (var inputStream = Files.newInputStream(classFile)) {
            reader = new ClassReader(inputStream);
        }
        var writer = new ClassWriter(reader, 0);
        reader.accept(new ClassVisitor(ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                var classVisitor = this;

//                var methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                var node = new MethodNode(ASM9, access, name, descriptor, signature, exceptions);
                return new ObsoleteMethodVisitor( node, classVisitor);
            }
        }, 0);
        Files.write(classFile, writer.toByteArray());
    }

    public static void rewriteAllFiles(Path directory) throws IOException {
        try (var stream = Files.walk(directory)) {
            for (var path : (Iterable<Path>) stream::iterator) {
                if (path.toString().endsWith(".class")) {
                    rewriteFile(path);
                }
            }
        }
    }

    private static class ObsoleteMethodVisitor extends MethodVisitor {
        private final MethodNode node;

        private final ClassVisitor classVisitor;

        private boolean obsolete;

        public ObsoleteMethodVisitor(MethodNode node, ClassVisitor classVisitor) {
            super(Opcodes.ASM9, node);
            this.node = node;
            this.classVisitor = classVisitor;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals(OBSOLETE_DESCRIPTOR)) {
                obsolete = true;
            }
            return super.visitAnnotation(descriptor, visible);
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            if (obsolete) {
                node.access |= ACC_SYNTHETIC;
            }
            node.accept(classVisitor.getDelegate());
        }
    }
}
