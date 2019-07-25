package tk.rdvdev2.ninjaplugin.tasks

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecSpec

class BuildNinjaTask extends DefaultTask {

    @InputDirectory
    public final DirectoryProperty sourceDir = project.objects.directoryProperty()

    @OutputFile
    final Provider<RegularFile> binaryFile = sourceDir.file("ninja" +(Os.isFamily(Os.FAMILY_WINDOWS) ? ".exe" : ""))

    @TaskAction
    void build() {
        ant.chmod(dir: sourceDir.get(), perm: "+x") {
            include(name: "**/*")
        }

        project.exec { ExecSpec execSpec ->
            execSpec.workingDir sourceDir.get()

            if (Os.isFamily(Os.FAMILY_WINDOWS)) {
                execSpec.executable "python"
                execSpec.args "configure.py"
                execSpec.args "--bootstrap"
            } else {
                execSpec.executable sourceDir.file("configure.py").get()
                execSpec.args "--bootstrap"
            }
        }
    }
}
