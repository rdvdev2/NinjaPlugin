package tk.rdvdev2.ninjaplugin.tasks

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import tk.rdvdev2.ninjaplugin.NinjaExtension

class GetNinjaTask extends DefaultTask{

    @Input
    final Provider<Boolean> build = project.extensions.getByType(NinjaExtension).build.map{it}

    @InputFile
    public final RegularFileProperty generatedBinary = project.objects.fileProperty()

    @OutputFile
    final Provider<RegularFile> outputBinary = project.layout.buildDirectory.file("ninja/ninja"+(Os.isFamily(Os.FAMILY_WINDOWS)?".exe":""))

    @TaskAction
    void get() {
        ant.copy(file: generatedBinary.get(), tofile: outputBinary.get())
    }
}
