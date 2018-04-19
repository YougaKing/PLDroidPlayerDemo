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


        JarInput pldroidJarInput
        //D:\StudioProject\PLDroidPlayer\PLDroidPlayerDemo\app\build\intermediates\classes\debug
        DirectoryInput sourceDirectoryInput
        try {
            inputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    String directoryName = directoryInput.name
                    mProject.logger.error "directoryName:" + directoryName + "-->" + directoryInput.file.absolutePath
                    sourceDirectoryInput = directoryInput
                }
                input.jarInputs.each { JarInput jarInput ->
                    String jarName = jarInput.name
                    String jarPath = jarInput.file.absolutePath
                    mProject.logger.error "jarName:" + jarName + "-->jarPath:" + jarPath
                    if (jarPath.endsWith("pldroid-player-2.1.1.jar")) {
                        pldroidJarInput = jarInput
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