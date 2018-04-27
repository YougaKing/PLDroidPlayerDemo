package cdn.youga.pldroid.javassist

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import javassist.ClassPool
import org.apache.commons.io.FileUtils

class CdnJavassistTransform extends Transform {

    Project mProject

    CdnJavassistTransform(Project project) {
        mProject = project;
    }

    @Override
    String getName() {
        return "CdnJavassistTransform"
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


        JarInput pldroidJarInput
        //D:\StudioProject\PLDroidPlayer\PLDroidPlayerDemo\app\build\intermediates\classes\debug
        DirectoryInput sourceDirectoryInput
        try {
            inputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    String directoryName = directoryInput.name
                    mProject.logger.error "directoryName:" + directoryName + "-->" + directoryInput.file.absolutePath
                    sourceDirectoryInput = directoryInput
                    def dst = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    FileUtils.copyDirectory(directoryInput.file, dst)
                }
                input.jarInputs.each { JarInput jarInput ->
                    String jarName = jarInput.name
                    String jarPath = jarInput.file.absolutePath
                    if (jarPath.endsWith("pldroid-player-2.1.1.jar")) {
                        pldroidJarInput = jarInput
                        mProject.logger.error "jarName:" + jarName + "-->jarPath:" + jarPath
                    } else {
                        def dst = outputProvider.getContentLocation(jarInput.name + DigestUtils.md5Hex(jarInput.file.getAbsolutePath()), jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        mProject.logger.error("dst:" + dst.getAbsolutePath())
                        FileUtils.copyFile(jarInput.file, dst)
                    }
                }
            }
        } catch (Exception e) {
            mProject.logger.error e.getMessage()
        }
        if (pldroidJarInput != null && sourceDirectoryInput != null) {
            PldroidInject.injectJar(pldroidJarInput, sourceDirectoryInput, outputProvider, mProject)
        }
        ClassPool.getDefault().clearImportedPackages()
        mProject.logger.error("JavassistTransform cast :" + (System.currentTimeMillis() - startTime) / 1000 + " secs")
    }
}