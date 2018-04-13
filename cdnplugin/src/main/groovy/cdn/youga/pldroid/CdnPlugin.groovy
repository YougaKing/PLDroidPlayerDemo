package cdn.youga.pldroid

import org.gradle.api.Plugin
import org.gradle.api.Project


class CdnPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def log = project.logger
        log.error "========================"
        log.error "CdnPlugin apply ()"
        log.error "========================"
        project.android.registerTransform(new CdnTransform(project))
    }
}