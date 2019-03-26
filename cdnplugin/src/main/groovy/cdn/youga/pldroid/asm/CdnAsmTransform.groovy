package cdn.youga.pldroid.asm

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import cdn.youga.pldroid.Util

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
class CdnAsmTransform extends Transform {

    Project mProject

    CdnAsmTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "CdnAsmTransform"
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
        //删除之前的输出
        if (outputProvider != null)
            outputProvider.deleteAll()

        inputs.each { TransformInput input ->

            input.jarInputs.each { JarInput jarInput ->
                String jarPath = jarInput.file.absolutePath

                if (jarPath.endsWith("pldroid-player-2.1.1.jar")) {
                    transformPldroidJar(jarInput, outputProvider)
                }
            }

        }

        mProject.logger.error("CdnAsmTransform cast :" + (System.currentTimeMillis() - startTime) / 1000 + " secs")
    }

    void transformPldroidJar(JarInput jarInput, TransformOutputProvider outputProvider) {

        String destName = jarInput.name

        def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (destName.endsWith(".jar")) {
            destName = destName.substring(0, destName.length() - 4)
        }
        // 获得输入文件
        File jarFile = jarInput.file
        // 获得输出文件
        File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

        //处理jar进行字节码注入处理TODO
        CdnProcessor.processJar(jarFile)

        FileUtils.copyFile(jarFile, dest)

        mProject.logger.info "copying\t${jarFile.absolutePath} \nto\t\t${dest.absolutePath}"
    }
}