package tk.rdvdev2.ninjaplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecSpec
import tk.rdvdev2.ninjaplugin.NinjaExtension

class RunNinjaTask extends DefaultTask {

    @InputFile
    final RegularFileProperty ninja = project.objects.fileProperty().convention(project.layout.buildDirectory.file('ninja/ninja'))

    @InputFile
    final RegularFileProperty master_ninja = project.objects.fileProperty().convention(project.layout.buildDirectory.file('ninja/build.ninja'))

    @InputDirectory
    final DirectoryProperty projects_ninja = project.objects.directoryProperty().convention(project.layout.buildDirectory.dir('ninja/buildfiles'))

    @Input ListProperty<String> targets = project.objects.listProperty(String)

    @TaskAction
    void run() {
        ant.chmod(file: ninja.asFile.get(), perm: '+x')
        project.exec { ExecSpec execSpec ->
            execSpec.executable(ninja.get())
            execSpec.args += "-C${project.buildDir}/ninja"
            execSpec.args += "-f${master_ninja.get().asFile.path}"
            execSpec.args += targets.get()
            execSpec.environment('NINJA_STATUS', project.extensions.findByType(NinjaExtension).ninja_status.get())
        }
    }
}
