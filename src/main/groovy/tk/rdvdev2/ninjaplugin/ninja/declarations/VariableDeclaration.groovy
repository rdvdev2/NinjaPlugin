package tk.rdvdev2.ninjaplugin.ninja.declarations

import org.gradle.api.Namer
import org.gradle.api.Project
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.provider.DefaultProviderFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.internal.impldep.com.beust.jcommander.internal.Lists
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class VariableDeclaration implements NinjaDeclaration {
    @Input final Provider<String> name
    @Input final Property<String> value

    @Inject
    VariableDeclaration(ObjectFactory objects, String name) {
        this.name = new DefaultProviderFactory().provider{name}
        this.value = objects.property(String)
    }

    VariableDeclaration(ObjectFactory objects, String name, String value) {
        this(objects, name)
        this.value.set(value)
    }

    void setValue(String value) {
        this.value.set(value)
    }

    String insert() {
        return "\${"+name.get()+"}"
    }

    @Override
    List<String> parse() {
        if (!value.present) throw new RuntimeException()
        List<String> ret = new ArrayList<>(1)
        ret.add(name.get()+" = "+value.get())
        return ret
    }

    @Override
    boolean equals(Object o) {
        if (o instanceof VariableDeclaration) {
            return name.equals(o.name)
        } else return false
    }
}
