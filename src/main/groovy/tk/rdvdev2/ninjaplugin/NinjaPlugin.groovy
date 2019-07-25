package tk.rdvdev2.ninjaplugin


import org.gradle.api.Plugin;
import org.gradle.api.Project

class NinjaPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create('ninja', NinjaExtension, project)
        NinjaTasks.register(project)
    }

}
