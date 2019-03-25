package cdn.youga.pldroid.asm

import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.LocalVariablesSorter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
class CdnProcessor {


    static File processJar(File jarFile) {
        def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
        if (optJar.exists())
            optJar.delete()
        def file = new JarFile(jarFile)

        Enumeration enumeration = file.entries()
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))


        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)

            InputStream inputStream = file.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)

            if (entryName.startsWith("com/qiniu/qplayer/mediaEngine/MediaPlayer") && entryName.endsWith(".class")) {
                println('entryName:' + entryName)
                def bytes = referHackWhenInit(inputStream)
                jarOutputStream.write(bytes)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()

        if (jarFile.exists()) {
            jarFile.delete()
        }
        optJar.renameTo(jarFile)
    }


    static byte[] referHackWhenInit(InputStream inputStream) {
        ClassReader reader = new ClassReader(inputStream)
        ClassWriter writer = new ClassWriter(reader, 0)
        ClassVisitor visitor = new MediaPlayerVisitor(Opcodes.ASM5, writer)
        reader.accept(visitor, ClassReader.EXPAND_FRAMES)
        return writer.toByteArray()
    }


    static class MediaPlayerVisitor extends ClassVisitor {
        String className
        String superName
        String[] interfaces

        MediaPlayerVisitor(int api, ClassVisitor visitor) {
            super(api, visitor)
        }

        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.className = name
            this.superName = superName
            this.interfaces = interfaces
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc,
                                  String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)
            println('CdnProcessor visitMethod:' + name + desc)
            mv = new MediaPlayerMethodVisitor(Opcodes.ASM5, mv, access, name, desc, signature, exceptions, className)
            return mv
        }
    }


    static class MediaPlayerMethodVisitor extends LocalVariablesSorter {
        int access
        String name, desc, signature, className
        String[] exceptions
        int aopVar

        MediaPlayerMethodVisitor(final int api, final MethodVisitor mv
                                 , int access, String name, String desc, String signature, String[] exceptions, String className) {
            super(api, access, desc, mv)
            this.access = access
            this.name = name
            this.desc = desc
            this.signature = signature
            this.exceptions = exceptions
            this.className = className
        }

        @Override
        void visitCode() {
            super.visitCode()
//            if (extension.aopClass && extension.methodStart) {
//                mv.visitLdcInsn(className);//类名
//                mv.visitLdcInsn(name)//方法名
//                mv.visitLdcInsn(desc)//参数列表及返回值类型
//                mv.visitMethodInsn(Opcodes.INVOKESTATIC, extension.aopClass, extension.methodStart, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)L${extension.aopClass};", false);
//                aopVar = newLocal(Type.getObjectType(extension.aopClass))
//                mv.visitVarInsn(Opcodes.ASTORE, aopVar)
//            }
        }

        @Override
        void visitInsn(int opcode) {
//            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
//                    || opcode == Opcodes.ATHROW) {
//                if (extension.aopClass && extension.methodEnd) {
//                    if (aopVar >= 0) {//在返回之前安插after 代码。
//                        mv.visitVarInsn(Opcodes.ALOAD, aopVar)
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, extension.aopClass, extension.methodEnd, "(L${extension.aopClass};)V", false);
//                    } else {
//                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, extension.aopClass, extension.methodEnd, "()V", false);
//                    }
//                }
//            }
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 4, maxLocals)
        }
    }

}