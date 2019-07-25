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
import org.gradle.internal.impldep.com.google.api.client.util.Lists

import javax.inject.Inject

class PoolDeclaration implements NinjaDeclaration {
    @Input final Provider<String> name
    @Input final Property<Integer> depth

    private final ObjectFactory objects

    @Inject
    PoolDeclaration(ObjectFactory objects, String name) {
        this.objects = objects
        this.name = new DefaultProviderFactory().provider{name}
        this.depth = objects.property(Integer)
    }

    void setDepth(int value) {
        depth.set(value)
    }

    @Override
    List<String> parse() {
        List<String> ret = new ArrayList<>()
        ret.add("pool "+name.get())
        if(!depth.present) throw new RuntimeException()
        ret.add("  "+new VariableDeclaration(objects, "depth", depth.get().toString()).parse().get(0))
        return ret
    }

    @Override
    boolean equals(Object o) {
        if (o instanceof PoolDeclaration) {
            return name.equals(o.name)
        } else return false
    }
}
