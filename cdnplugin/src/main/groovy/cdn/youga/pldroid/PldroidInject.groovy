package cdn.youga.pldroid

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import com.qiniu.qplayer.mediaEngine.MediaPlayer
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


class PldroidInject {


    static void injectJar(JarInput jarInput, TransformOutputProvider outputProvider, Project project) {
        String jarPath = jarInput.file.absolutePath

        if (!jarPath.endsWith("pldroid-player-2.1.1.jar")) return

        ClassPool pool = ClassPool.getDefault()
        pool.appendClassPath(jarPath)


        File jarFile = new File(jarPath)
        // jar包解压后的保存路径
        String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
        // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
        JarZipUtil.unzipJar(jarPath, jarZipDir)

        // 删除原来的jar包
//        jarFile.delete()
        // 注入代码
        pool.appendClassPath(jarZipDir)


        injectClass(jarZipDir, pool, project)
        // 从新打包jar
//        JarZipUtil.zipJar(jarFile, jarZipDir, jarPath)

        // 删除目录
//        FileUtils.deleteDirectory(new File(jarZipDir))
    }


    private
    static void injectClass(String jarZipDir, ClassPool pool, Project project) {
        project.logger.error("jarZipDir:" + jarZipDir)
        CtClass clazz = pool.get("com.qiniu.qplayer.mediaEngine.MediaPlayer")

        if (clazz.isFrozen()) {
            clazz.defrost()
        }
//
//        for (int i = 0; i < clazz.declaredMethods.size(); i++) {
//            def method = clazz.declaredMethods[i]
//            project.logger.error(method.name)
//            if (method.parameterTypes != null) {
//                for (int j = 0; j < method.parameterTypes.size(); j++) {
//                    project.logger.error(method.parameterTypes[j].name)
//                }
//            }
//        }

        CtClass[] params = [pool.get(String.class.getName()), pool.get(Map.class.getName())] as CtClass[]
        CtMethod setDataSource = clazz.getDeclaredMethod("a", params)
        project.logger.error("setDataSource:" + setDataSource)

        setDataSource.insertAfter("cdn.youga.instrument.MediaPlayerInjection.setDataSource(\$1, \$2, \$0);")

        clazz.writeFile(jarZipDir)
        clazz.detach()
    }


    private static void exportJar(JarInput jarInput, TransformOutputProvider outputProvider) {
        // 重命名输出文件（同目录copyFile会冲突）
        def jarName = jarInput.name
        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }
        def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
//        project.logger.error("dest = "+dest.absolutePath+"="+dest.exists())
//        project.logger.error("jarInput.file = "+jarInput.file.absolutePath+"="+jarInput.file.exists())
        dest.mkdirs()//需要先创建文件才可以哦
        dest.createNewFile()
        FileUtils.copyFile(jarInput.file, dest)
    }

}