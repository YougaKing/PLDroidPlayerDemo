package cdn.youga.pldroid.javassist

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
class CdnJavassistTransform extends Transform {

    Project mProject

    CdnJavassistTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "cdnJavassistTransform"
    }

    //需要处理的数据类型，有两种枚举类型
    //CLASSES和RESOURCES，CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    //    指Transform要操作内容的范围，官方文档Scope有7种类型：
    //
    //    EXTERNAL_LIBRARIES        只有外部库
    //    PROJECT                       只有项目内容
    //    PROJECT_LOCAL_DEPS            只有项目的本地依赖(本地jar)
    //    PROVIDED_ONLY                 只提供本地或远程依赖项
    //    SUB_PROJECTS              只有子项目。
    //    SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
    //    TESTED_CODE                   由当前变量(包括依赖项)测试的代码
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    //指明当前Transform是否支持增量编译
    @Override
    boolean isIncremental() {
        return false
    }

    //    Transform中的核心方法，
    //    inputs中是传过来的输入流，其中有两种格式，一种是jar包格式一种是目录格式。
    //    outputProvider 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        def startTime = System.currentTimeMillis()

        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider

        File pldroidJarFile
        try {
            inputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput directoryInput ->

                    File dst = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                    mProject.logger.error "directoryName:" + directoryInput.name + "directoryPath" + directoryInput.file.absolutePath + "\ndst:" + dst.absolutePath

                    FileUtils.copyDirectory(directoryInput.file, dst)

                    ClassPool.getDefault().appendClassPath(dst.absolutePath)
                }
                input.jarInputs.each { JarInput jarInput ->

                    File dst = outputProvider.getContentLocation(jarInput.name + DigestUtils.md5Hex(jarInput.file.getAbsolutePath()), jarInput.contentTypes, jarInput.scopes, Format.JAR)

                    mProject.logger.error "jarName:" + jarInput.name + "jarPath:" + jarInput.file.absolutePath + "\ndst:" + dst.absolutePath

                    FileUtils.copyFile(jarInput.file, dst)

                    ClassPool.getDefault().appendClassPath(dst.absolutePath)

                    if (pldroidJarFile == null && isPldroidJar(dst)) pldroidJarFile = dst
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter()
            e.printStackTrace(new PrintWriter(sw))
            mProject.logger.error "error:" + sw.toString()
        }

        try {
            if (pldroidJarFile != null) PldroidInject.injectRebirthJar(pldroidJarFile, mProject)
        } catch (Exception e) {
            StringWriter sw = new StringWriter()
            e.printStackTrace(new PrintWriter(sw))
            mProject.logger.error "error:" + sw.toString()
        }

        ClassPool.getDefault().clearImportedPackages()
        mProject.logger.debug("cdnJavassistTransform cast :" + (System.currentTimeMillis() - startTime) / 1000 + " secs")
    }


    static boolean isPldroidJar(File file) {
        JarFile jarFile = new JarFile(file)
        Enumeration<JarEntry> enumeration = jarFile.entries()

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            if (jarEntry.directory) {
                continue
            }
            String entryName = jarEntry.getName()

            if (entryName == "com/qiniu/qplayer/mediaEngine/MediaPlayer.class") {
                return true
            }
        }
        jarFile.close()
        return false
    }
}