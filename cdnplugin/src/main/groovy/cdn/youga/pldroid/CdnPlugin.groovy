package cdn.youga.pldroid

import cdn.youga.pldroid.asm.CdnAsmTransform
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CdnPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def log = project.logger
        log.error "========================"
        log.error "CdnPlugin apply ()"
        log.error "========================"

        //AppExtension就是build.gradle中android{...}这一块
        def android = project.extensions.getByType(AppExtension)

        //注册一个Transform
        def classTransform = new CdnAsmTransform(project)
        android.registerTransform(classTransform)
    }
}