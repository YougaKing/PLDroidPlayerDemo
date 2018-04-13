package cdn.youga.pldroid

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


class PldroidInject {


    static void injectJar(JarInput jarInput, TransformOutputProvider outputProvider, Project project) {
        String jarPath = jarInput.file.absolutePath

        if (!jarPath.endsWith("pldroid-player-2.1.1.jar")) return

        ClassPool pool = ClassPool.getDefault()
        pool.appendClassPath(jarPath)

        String projectName = project.rootProject.name



        File jarFile = new File(jarPath)
        // jar包解压后的保存路径
        String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
        // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
        List classNameList = JarZipUtil.unzipJar(jarPath, jarZipDir)

        // 删除原来的jar包
        jarFile.delete()
        // 注入代码
        pool.appendClassPath(jarZipDir)


        for (String className : classNameList) {
            if (className == "com.qiniu.qplayer.mediaEngine.MediaPlayer") {
                injectClass(className, jarZipDir)
            }
        }
        // 从新打包jar
        JarZipUtil.zipJar(jarFile, jarZipDir, jarPath)

        // 删除目录
        FileUtils.deleteDirectory(new File(jarZipDir))
    }


    private static void injectClass(String className, String jarZipDir, ClassPool pool) {
        println(jarZipDir)
        CtClass c = pool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }
        println(className)
        for (int i = 0; i < c.declaredMethods.size(); i++) {
            def method = c.declaredMethods[i]
            println(method.name)
            if (method.name.contains("init")) {
                method.insertAfter("System.out.println(\"测试插入\");")
                println("插入成功")//测试成功的插入代码
            }
        }
        c.writeFile(jarZipDir)
        c.detach()
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