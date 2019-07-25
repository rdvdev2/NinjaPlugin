package tk.rdvdev2.ninjaplugin.tasks;

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import tk.rdvdev2.ninjaplugin.ninja.NinjaFileWriter;
import tk.rdvdev2.ninjaplugin.ninja.NinjaSourceSet
import tk.rdvdev2.ninjaplugin.ninja.declarations.CommentDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.NinjaDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.ReferenceDeclaration;

class GenerateMasterNinjaFileTask extends DefaultTask {

    @Nested final SetProperty<NinjaSourceSet> sourceSets = project.objects.setProperty(NinjaSourceSet)

    @OutputFile final Provider<RegularFile> output = project.layout.buildDirectory.file('ninja/build.ninja')

    @TaskAction
    void generate() {
        List<NinjaDeclaration> declarations = generateDeclarations()
        NinjaFileWriter writer = new NinjaFileWriter(project.objects, output.get(), declarations)
        writer.write()
    }

    List<NinjaDeclaration> generateDeclarations() {
        List<NinjaDeclaration> ret = new ArrayList<>(sourceSets.get().size())
        ret.add(new CommentDeclaration(project.objects, 'This is the main ninja file and includes all the source sets of the project unless they explicitly state to don\'t be included'))
        sourceSets.get().forEach {
            if (it.includeInMaster.get()) {
                def reference = new ReferenceDeclaration(project.objects)
                GenerateNinjaFileTask task = project.tasks.getByName('generate'+it.name.capitalize()+"NinjaFile") as GenerateNinjaFileTask
                reference.file.set task.outputFile
                reference.type = 'include'
                ret.add(reference)
            }
        }
        return ret
    }

    void setSourceSets(Iterable<NinjaSourceSet> value) {
        sourceSets.set(value)
    }
}
