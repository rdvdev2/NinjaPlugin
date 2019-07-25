package tk.rdvdev2.ninjaplugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import tk.rdvdev2.ninjaplugin.NinjaExtension

class DownloadNinjaSourceTask extends DefaultTask {

    @Input
    final Provider<String> version = project.extensions.getByType(NinjaExtension).version.map{it}

    @OutputDirectory
    final Provider<Directory> sourceDir = project.layout.buildDirectory.dir("ninja/source/ninja-"+version.get())

    @TaskAction
    void download() {
        File zip = project.file("${project.buildDir}/ninja/source/download/v"+version.get()+".zip")
        URL source = new URL("https://github.com/ninja-build/ninja/archive/v"+version.get()+".zip")
        if (!zip.exists()) {
            zip.parentFile.mkdirs()
            ant.get(src: source, dest: zip)
        }
        ant.unzip(src: zip, dest: sourceDir.get().asFile.parent)
    }
}
