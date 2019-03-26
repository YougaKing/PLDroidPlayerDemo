package cdn.youga.pldroid.asm

import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
class CdnProcessor {

    static File processJar(File originFile, File tempFile, Project project) {
        JarFile jarFile = new JarFile(originFile)
        Enumeration enumeration = jarFile.entries()
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tempFile))
        //用于保存
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)
            InputStream inputStream = jarFile.getInputStream(jarEntry)

            //插桩class
            if (isMediaPlayerClass(entryName)) {
                //class文件处理
                project.logger.error(entryName)

                jarOutputStream.putNextEntry(zipEntry)
                ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                ClassVisitor cv = new MediaPlayerVisitor(classWriter, project)
                classReader.accept(cv, EXPAND_FRAMES)
                byte[] code = classWriter.toByteArray()
                jarOutputStream.write(code)
            } else {
                jarOutputStream.putNextEntry(zipEntry)
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }
        //结束
        jarOutputStream.close()
        jarFile.close()
    }


    static boolean isMediaPlayerClass(String name) {
        //只处理需要的class文件
        return "com/qiniu/qplayer/mediaEngine/MediaPlayer.class" == name
    }
}