package cdn.youga.pldroid

import cdn.youga.pldroid.javassist.CdnJavassistTransform
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CdnPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.logger.error "========================"
        project.logger.error "CdnPlugin apply ()"
        project.logger.error "========================"

        //AppExtension就是build.gradle中android{...}这一块
        def android = project.extensions.getByType(AppExtension)

        //注册一个Transform
//        def classTransform = new CdnAsmTransform(project)
        def classTransform = new CdnJavassistTransform(project)
        android.registerTransform(classTransform)
    }
}