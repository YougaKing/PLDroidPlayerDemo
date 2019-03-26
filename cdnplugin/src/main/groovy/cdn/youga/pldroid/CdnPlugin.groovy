package cdn.youga.pldroid

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import cdn.youga.pldroid.javassist.PldroidInject

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
class CdnPlugin extends Transform implements Plugin<Project> {
    Project mProject

    @Override
    void apply(Project project) {
        mProject = project
        project.logger.error "========================"
        project.logger.error "CdnPlugin apply ()"
        project.logger.error "========================"

        //AppExtension就是build.gradle中android{...}这一块
        def android = project.extensions.getByType(AppExtension)

        //注册一个Transform
        android.registerTransform(this)
    }


    @Override
    String getName() {
        return "cdnPluginTransform"
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

        File pldroidJarOriginFile
        File pldroidJarTempFile
        File pldroidJarDestFile
        try {
            //删除之前的输出
//            outputProvider.deleteAll()

            inputs.each { TransformInput input ->
                //遍历directoryInputs 处理文件目录下的class文件
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    ClassPool.getDefault().appendClassPath(directoryInput.file.absolutePath)
                    FileUtils.copyDirectory(directoryInput.file, dest)
                }

                //遍历jarInputs  处理Jar中的class文件
                input.jarInputs.each { JarInput jarInput ->
                    File dest = outputProvider.getContentLocation(jarInput.name + DigestUtils.md5Hex(jarInput.file.getAbsolutePath()), jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    if (Util.isPldroidJar(jarInput.file)) {
                        def jarName = jarInput.file.name
                        if (jarName.endsWith(".jar")) {
                            jarName = jarName.substring(0, jarName.length() - 4)
                        }
                        pldroidJarOriginFile = jarInput.file
                        pldroidJarTempFile = new File(dest.getParent() + File.separator + jarName + ".jar")
                        pldroidJarDestFile = dest
                        //避免上次的缓存被重复插入
                        if (pldroidJarTempFile.exists()) {
                            pldroidJarTempFile.delete()
                        }
                    } else {
                        FileUtils.copyFile(jarInput.file, dest)
                        ClassPool.getDefault().appendClassPath(dest.absolutePath)
                    }
                }
            }

            if (pldroidJarTempFile != null && pldroidJarDestFile != null && pldroidJarOriginFile != null) {
                PldroidInject.processJar(pldroidJarOriginFile, pldroidJarTempFile, mProject)
//                CdnProcessor.processJar(pldroidJarOriginFile, pldroidJarTempFile, mProject)

                FileUtils.copyFile(pldroidJarTempFile, pldroidJarDestFile)
                pldroidJarTempFile.delete()
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter()
            e.printStackTrace(new PrintWriter(sw))
            mProject.logger.error "error:" + sw.toString()
        }
        mProject.logger.error("CdnPluginTransform cast :" + (System.currentTimeMillis() - startTime) / 1000 + " secs")
    }
}