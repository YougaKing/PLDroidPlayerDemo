package cdn.youga.pldroid.javassist

import cdn.youga.pldroid.Util
import com.android.utils.FileUtils
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
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

    static void injectRebirthJar(File originFile, File tempFile, Project project) {
        FileUtils.copyFile(originFile, tempFile)
        ClassPool.getDefault().appendClassPath(tempFile.absolutePath)

        // jar包解压后的保存路径
        String jarZipDir = tempFile.getParent() + "/" + tempFile.getName().replace('.jar', '')
        // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
        JarZipUtil.unzipJar(tempFile.absolutePath, jarZipDir)

        // 注入代码
        CtClass mediaPlayer = ClassPool.getDefault().get("com.qiniu.qplayer.mediaEngine.MediaPlayer")
        CtClass ctClass = injectMediaPlayerClass(mediaPlayer, project)
        ctClass.writeFile(jarZipDir)
        ctClass.detach()
        ctClass.defrost()

        // 重新打包jar
        JarZipUtil.zipJar(tempFile, new File(jarZipDir))
        // 删除目录
//        FileUtils.deleteDirectory(new File(jarZipDir))
    }

    static void processJar(File originFile, File tempFile, Project project) {
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
            if (Util.isMediaPlayerClass(entryName)) {
                //class文件处理
                project.logger.error(entryName)

                jarOutputStream.putNextEntry(zipEntry)

                ClassPool pool = ClassPool.getDefault()
                CtClass ctClass = pool.makeClass(inputStream, false)
                ctClass = injectMediaPlayerClass(ctClass, project)

                jarOutputStream.write(ctClass.toBytecode())
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


    static CtClass injectMediaPlayerClass(CtClass mediaPlayer, Project project) {
        ClassPool pool = ClassPool.getDefault()
        if (mediaPlayer.isFrozen()) mediaPlayer.defrost()
        mediaPlayer.stopPruning(true)

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