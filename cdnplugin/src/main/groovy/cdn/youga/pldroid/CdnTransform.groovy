package cdn.youga.pldroid

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project
import javassist.ClassPool

class CdnTransform extends Transform {

    Project mProject

    CdnTransform(Project project) {
        mProject = project;
    }

    @Override
    String getName() {
        return "CdnTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        def startTime = System.currentTimeMillis()

        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider


        try {
            inputs.each { TransformInput input ->
                //对 jar包 类型的inputs 进行遍历
                input.jarInputs.each { JarInput jarInput ->
                    def jarName = jarInput.name
                    mProject.logger.error("jarName:" + jarName + "-->" + jarInput.file.getAbsolutePath())

                    //这里处理自定义的逻辑
                    PldroidInject.injectJar(jarInput, outputProvider, mProject)
                }
            }
        } catch (Exception e) {
            mProject.logger.error e.getMessage()
        }
        ClassPool.getDefault().clearImportedPackages()
        mProject.logger.error("JavassistTransform cast :" + (System.currentTimeMillis() - startTime) / 1000 + " secs")
    }
}