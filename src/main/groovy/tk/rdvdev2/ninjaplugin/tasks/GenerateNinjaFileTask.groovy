package tk.rdvdev2.ninjaplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import tk.rdvdev2.ninjaplugin.ninja.NinjaFileWriter
import tk.rdvdev2.ninjaplugin.ninja.NinjaSourceSet
import tk.rdvdev2.ninjaplugin.ninja.declarations.NinjaDeclaration

class GenerateNinjaFileTask extends DefaultTask {

    @Nested
    final Property<NinjaSourceSet> sourceSet = project.objects.property(NinjaSourceSet)

    @OutputFile
    final RegularFileProperty outputFile = project.objects.fileProperty().convention(sourceSet.map{project.layout.buildDirectory.file("ninja/buildfiles/"+it.name+".ninja").get()})

    @TaskAction
    generate() {
        List<NinjaDeclaration> declarations = sourceSet.get().declarations
        NinjaFileWriter writer = new NinjaFileWriter(project.objects, outputFile.get(), declarations)
        writer.write()
    }
}
