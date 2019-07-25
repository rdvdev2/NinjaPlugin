package tk.rdvdev2.ninjaplugin.tasks

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import tk.rdvdev2.ninjaplugin.NinjaExtension

class DownloadNinjaBinaryTask extends DefaultTask {

    @Input
    final Provider<String> version = project.extensions.findByType(NinjaExtension).version.map{it}

    @OutputFile
    final Provider<RegularFile> binaryFile = project.layout.buildDirectory.file("ninja/versions/v"+version.get()+"/ninja"+(Os.isFamily(Os.FAMILY_WINDOWS)? ".exe" : ""))

    @TaskAction
    def download() {
        File zip = project.file("${project.buildDir}/ninja/download/v"+version.get()+".zip")
        URL source = parseSourceURL()
        if (!zip.exists()) {
            zip.parentFile.mkdirs()
            ant.get(src: source, dest: zip)
        }
        if (!binaryFile.get().asFile.exists()) {
            binaryFile.get().asFile.parentFile.mkdirs()
            ant.unzip(src: zip, dest: binaryFile.get().asFile.parentFile)
        }
    }

    URL parseSourceURL() {
        String filename
        if (Os.isFamily(Os.FAMILY_UNIX)) {
            filename = "ninja-linux.zip"
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            filename = "ninja-mac.zip"
        } else if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            filename = "ninja-win.zip"
        }
        return new URL("https://github.com/ninja-build/ninja/releases/download/v"+version.get()+"/"+filename)
    }
}
