package tk.rdvdev2.ninjaplugin

import org.gradle.api.Project
import tk.rdvdev2.ninjaplugin.ninja.NinjaSourceSet
import tk.rdvdev2.ninjaplugin.tasks.BuildNinjaTask
import tk.rdvdev2.ninjaplugin.tasks.DownloadNinjaBinaryTask
import tk.rdvdev2.ninjaplugin.tasks.DownloadNinjaSourceTask
import tk.rdvdev2.ninjaplugin.tasks.GenerateMasterNinjaFileTask
import tk.rdvdev2.ninjaplugin.tasks.GenerateNinjaFileTask
import tk.rdvdev2.ninjaplugin.tasks.GetNinjaTask
import tk.rdvdev2.ninjaplugin.tasks.RunNinjaTask

class NinjaTasks {
    static register(Project project) {
        def getNinja = project.tasks.register('getNinja', GetNinjaTask)
        def downloadNinjaBinary = project.tasks.register('downloadNinjaBinary', DownloadNinjaBinaryTask)
        def buildNinja = project.tasks.register('buildNinja', BuildNinjaTask)
        def downloadNinjaSource = project.tasks.register('downloadNinjaSource', DownloadNinjaSourceTask)
        def buildTask = project.tasks.register('build', RunNinjaTask)

        getNinja.configure {
            NinjaExtension extension = project.extensions.findByType(NinjaExtension)
            if (extension.build.get()) {
                it.generatedBinary.set buildNinja.flatMap { it.binaryFile }
                it.dependsOn buildNinja
            } else {
                it.generatedBinary.set downloadNinjaBinary.flatMap { it.binaryFile }
                it.dependsOn downloadNinjaBinary
            }
        }

        buildNinja.configure {
            it.sourceDir.set downloadNinjaSource.flatMap { it.sourceDir }
            it.dependsOn downloadNinjaSource
        }

        buildTask.configure {
            it.dependsOn 'generateMasterNinjaFile'
            it.dependsOn getNinja
        }

        project.defaultTasks += 'build'

        project.afterEvaluate {
            NinjaExtension extension = project.extensions.findByType(NinjaExtension)
            List<NinjaSourceSet> sourceSets = extension.getSourceSets()
            def generateMasterNinjaFile = project.tasks.register('generateMasterNinjaFile', GenerateMasterNinjaFileTask)
            generateMasterNinjaFile.configure {
                it.sourceSets = sourceSets
            }
            sourceSets.forEach { sourceSet ->
                if (sourceSet.name.equalsIgnoreCase('master')) throw new RuntimeException()
                def generateNinjaFile = project.tasks.register('generate'+sourceSet.name.capitalize()+"NinjaFile", GenerateNinjaFileTask)
                generateNinjaFile.configure { task ->
                    task.sourceSet.set sourceSet
                }
                generateMasterNinjaFile.configure {
                    it.dependsOn generateNinjaFile
                }
            }
        }
    }
}
