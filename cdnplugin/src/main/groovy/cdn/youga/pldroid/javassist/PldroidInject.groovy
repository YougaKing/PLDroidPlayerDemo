package cdn.youga.pldroid.javassist

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
class PldroidInject {


    static void injectRebirthJar(File pldroidJarFile, Project project) {
        // jar包解压后的保存路径
        String jarZipDir = pldroidJarFile.getParent() + "/" + pldroidJarFile.getName().replace('.jar', '')
        // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
        JarZipUtil.unzipJar(pldroidJarFile.absolutePath, jarZipDir)

        // 注入代码
        CtClass ctClass = injectClass(project)
        ctClass.writeFile(jarZipDir)
        ctClass.detach()

        // 重新打包jar
        JarZipUtil.zipJar(pldroidJarFile, new File(jarZipDir))

        // 删除目录
//        FileUtils.deleteDirectory(new File(jarZipDir))
    }

    static File injectReviveJar(File pldroidJarFile, Project project) {
        File optJar = new File(pldroidJarFile.getParent(), pldroidJarFile.name + ".opt")
        if (optJar.exists())
            optJar.delete()
        JarFile jarFile = new JarFile(pldroidJarFile)

        Enumeration<JarEntry> enumeration = jarFile.entries()
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))


        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)

            InputStream inputStream = jarFile.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)

            if (entryName == "com/qiniu/qplayer/mediaEngine/MediaPlayer.class") {
                // 注入代码
                CtClass ctClass = injectClass(project)
                ctClass.detach()

                jarOutputStream.write(ctClass.toBytecode())
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        jarFile.close()

        if (pldroidJarFile.exists()) pldroidJarFile.delete()

        optJar.renameTo(pldroidJarFile)
    }


    static CtClass injectClass(Project project) {
        ClassPool pool = ClassPool.getDefault()

        CtClass mediaPlayer = pool.get("com.qiniu.qplayer.mediaEngine.MediaPlayer")

        if (mediaPlayer.isFrozen()) mediaPlayer.defrost()

        CtClass[] params = [pool.get(String.class.getName()), pool.get(Map.class.getName())] as CtClass[]
        CtMethod setDataSource = mediaPlayer.getDeclaredMethod("a", params)
        project.logger.error("setDataSource:" + setDataSource)
        setDataSource.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.setDataSource(\$1, \$2, \$0);")

        CtMethod prepareAsync = mediaPlayer.getDeclaredMethod("b")
        project.logger.error("prepareAsync:" + prepareAsync)
        prepareAsync.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.prepareAsync(\$0);")

        CtMethod start = mediaPlayer.getDeclaredMethod("c")
        project.logger.error("start:" + start)
        start.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.start(\$0);")

        CtMethod pause = mediaPlayer.getDeclaredMethod("d")
        project.logger.error("pause:" + pause)
        pause.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.pause(\$0);")

        CtMethod stop = mediaPlayer.getDeclaredMethod("e")
        project.logger.error("stop:" + stop)
        stop.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.stop(\$0);")

        params = [CtClass.intType] as CtClass[]
        CtMethod seekTo = mediaPlayer.getDeclaredMethod("a", params)
        project.logger.error("seekTo:" + seekTo)
        seekTo.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.seekTo(\$1,\$0);")

        params = [pool.get(Object.class.getName()), CtClass.intType, CtClass.intType, CtClass.intType, pool.get(Object.class.getName())] as CtClass[]
        CtMethod postEventFromNative = mediaPlayer.getDeclaredMethod("postEventFromNative", params)
        project.logger.error("postEventFromNative:" + postEventFromNative)
        postEventFromNative.insertBefore("cdn.youga.instrument.MediaPlayerInstrument.postEventFromNative(\$1, \$2, \$3,\$4, \$5);")


        CtClass mediaPlayerInstrument = pool.get("cdn.youga.instrument.MediaPlayerInstrument")
        mediaPlayerInstrument.detach()
        mediaPlayerInstrument.defrost()

        return mediaPlayer
    }
}