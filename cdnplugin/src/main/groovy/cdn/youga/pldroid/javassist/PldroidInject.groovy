package cdn.youga.pldroid.javassist

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project


class PldroidInject {


    static void injectJar(JarInput pldroidJarInput, DirectoryInput sourceDirectoryInput,
                          TransformOutputProvider outputProvider, Project project) {
        String jarPath = pldroidJarInput.file.absolutePath
        String sourcePath = sourceDirectoryInput.file.absolutePath


        ClassPool pool = ClassPool.getDefault()
        pool.appendClassPath(jarPath)
        pool.appendClassPath(sourcePath)


        File jarFile = new File(jarPath)
        // jar包解压后的保存路径
        String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
        // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
        JarZipUtil.unzipJar(jarPath, jarZipDir)

        // 删除原来的jar包
        jarFile.delete()
        // 注入代码
        pool.appendClassPath(jarZipDir)


        injectClass(jarZipDir, pool, project)
        // 从新打包jar
        JarZipUtil.zipJar(jarFile, new File(jarZipDir))

//        // 删除目录
//        FileUtils.deleteDirectory(new File(jarZipDir))
    }


    private
    static void injectClass(String jarZipDir, ClassPool pool, Project project) {
        project.logger.error("jarZipDir:" + jarZipDir)
        CtClass clazz = pool.get("com.qiniu.qplayer.mediaEngine.MediaPlayer")

        if (clazz.isFrozen()) {
            clazz.defrost()
        }

        CtClass[] params = [pool.get(String.class.getName()), pool.get(Map.class.getName())] as CtClass[]
        CtMethod setDataSource = clazz.getDeclaredMethod("a", params)
        project.logger.error("setDataSource:" + setDataSource)
        setDataSource.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.setDataSource(\$1, \$2, \$0);")

        CtMethod prepareAsync = clazz.getDeclaredMethod("b")
        project.logger.error("prepareAsync:" + prepareAsync)
        prepareAsync.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.prepareAsync(\$0);")

        CtMethod start = clazz.getDeclaredMethod("c")
        project.logger.error("start:" + start)
        start.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.start(\$0);")

        CtMethod pause = clazz.getDeclaredMethod("d")
        project.logger.error("pause:" + pause)
        pause.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.pause(\$0);")

        CtMethod stop = clazz.getDeclaredMethod("e")
        project.logger.error("stop:" + stop)
        stop.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.stop(\$0);")

        params = [CtClass.intType] as CtClass[]
        CtMethod seekTo = clazz.getDeclaredMethod("a", params)
        project.logger.error("seekTo:" + seekTo)
        seekTo.insertAfter("cdn.youga.instrument.MediaPlayerInstrument.seekTo(\$1,\$0);")

        params = [pool.get(Object.class.getName()), CtClass.intType, CtClass.intType, CtClass.intType, pool.get(Object.class.getName())] as CtClass[]
        CtMethod postEventFromNative = clazz.getDeclaredMethod("postEventFromNative", params)
        project.logger.error("postEventFromNative:" + postEventFromNative)
        postEventFromNative.insertBefore("cdn.youga.instrument.MediaPlayerInstrument.postEventFromNative(\$1, \$2, \$3,\$4, \$5);")

        clazz.writeFile(jarZipDir)
        clazz.detach()
    }
}