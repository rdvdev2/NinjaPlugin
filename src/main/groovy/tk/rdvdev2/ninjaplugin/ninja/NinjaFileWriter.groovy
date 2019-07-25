package tk.rdvdev2.ninjaplugin.ninja

import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.internal.impldep.com.beust.jcommander.internal.Lists
import tk.rdvdev2.ninjaplugin.ninja.declarations.BuildDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.CommentDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.DefaultDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.NinjaDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.PoolDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.ReferenceDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.RuleDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.VariableDeclaration

class NinjaFileWriter {
    private final RegularFile file
    private final Collection<NinjaDeclaration> declarations
    private final ObjectFactory objects

    NinjaFileWriter(ObjectFactory objects, RegularFile file, Collection<NinjaDeclaration> declarations) {
        this.objects = objects
        this.file = file
        this.declarations = declarations
    }

    void write() {
        def comments = declarations.findAll { it instanceof CommentDeclaration }
        def references = declarations.findAll { it instanceof ReferenceDeclaration }
        def defaults = declarations.findAll { it instanceof DefaultDeclaration}
        def vars = declarations.findAll { it instanceof VariableDeclaration }
        def pools = declarations.findAll { it instanceof PoolDeclaration }
        def rules = declarations.findAll { it instanceof RuleDeclaration }
        def builds = declarations.findAll {it instanceof BuildDeclaration }

        List<String> contents = new ArrayList<>(declarations.size()*2)
        contents.add("")
        contents.addAll(new CommentDeclaration(objects, "This ninja file was autogenerated by Gradle using NinjaPlugin").parse())
        if(!comments.empty) {
            contents.add("")
            comments.forEach { contents.addAll(it.parse()) }
        }
        contents.add("")
        contents.add("RESERVED") // Reserved for ninja_required_version
        if(!references.empty) {
            contents.add("")
            references.forEach { contents.addAll(it.parse()) }
        }
        if(!vars.empty) {
            contents.add("")
            contents.add("")
            contents.addAll(new CommentDeclaration(objects, "Variable declarations").parse())
            contents.add("")
            vars.forEach { contents.addAll(it.parse()) }
        }
        if(!pools.empty) {
            contents.add("")
            contents.add("")
            contents.addAll(new CommentDeclaration(objects, "Pool declarations").parse())
            contents.add("")
            pools.forEach { contents.addAll(it.parse()) }
        }
        if(!rules.empty) {
            contents.add("")
            contents.add("")
            contents.addAll(new CommentDeclaration(objects, "Rule declarations").parse())
            rules.forEach { contents.add(""); contents.addAll(it.parse()) }
        }
        if(!builds.empty) {
            contents.add("")
            contents.add("")
            contents.addAll(new CommentDeclaration(objects, "Build declarations").parse())
            builds.forEach { contents.add(""); contents.addAll(it.parse()) }
        }
        if(!defaults.empty) {
            contents.add("")
            contents.add("")
            contents.addAll(new CommentDeclaration(objects, "Default build targets").parse())
            contents.add("")
            defaults.forEach { contents.addAll(it.parse()) }
        }
        contents.add("")
        setRequiredNinja(contents)

        File outputFile = file.asFile
        outputFile.parentFile.mkdirs()
        if (outputFile.exists()) outputFile.delete()
        outputFile.createNewFile()
        contents.forEach { outputFile.append(it+"\n") }
    }

    void setRequiredNinja(List<String> ninjaFile) {
        int minimumVersion = 100
        int reservedLineIndex = 0
        for (int i = 0; i < ninjaFile.size(); i++) {
            String line = ninjaFile.get(i)
            int featureVersion = 0
            if (line.equals("RESERVED")) {
                reservedLineIndex = i
                continue
            } else if (line.contains("deps =")) {
                featureVersion = 130
            } else if (line.contains("pool = console")) {
                featureVersion = 150
            } else if (line.startsWith("pool ")) {
                featureVersion = 110
            } else if (line ==~ /build.*[|].*[:].*/) {
                featureVersion = 170
            } else if (line.contains("msvc_deps_prefix = ")) {
                featureVersion = 150
            }
            minimumVersion = Math.max(minimumVersion, featureVersion)
        }
        String minimumVersionString = minimumVersion.toString()[0] + "." + minimumVersion.toString()[1] + "." + minimumVersion.toString()[2]
        ninjaFile.set(reservedLineIndex, new VariableDeclaration(objects, "required_ninja_version", minimumVersionString).parse().get(0))
    }
}
