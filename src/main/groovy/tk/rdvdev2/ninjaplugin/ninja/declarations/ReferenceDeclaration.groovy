package tk.rdvdev2.ninjaplugin.ninja.declarations

import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

class ReferenceDeclaration implements NinjaDeclaration {
    @Input final Property<TypeEnum> type
    @Nested final RegularFileProperty file

    ReferenceDeclaration(ObjectFactory objects) {
        this.type = objects.property(TypeEnum)
        this.file = objects.fileProperty()
    }

    void setType(TypeEnum value) {
        type.set(value)
    }

    void setType(String value) {
        setType(TypeEnum.valueOf(value))
    }

    void setFile(RegularFile value) {
        file.set(value)
    }

    @Override
    List<String> parse() {
        if (!type.present || !file.present) throw new RuntimeException()
        return Arrays.asList(type.get().name()+" "+file.get().asFile.path)
    }

    enum TypeEnum {subninja, include}
}
