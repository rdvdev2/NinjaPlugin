package tk.rdvdev2.ninjaplugin.ninja.declarations

import org.gradle.api.Namer
import org.gradle.api.Project
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.provider.DefaultProviderFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.internal.impldep.com.beust.jcommander.internal.Lists
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

class RuleDeclaration implements NinjaDeclaration {
    @Input final Provider<String> rulename
    @Input final Property<String> command
    @Optional @Input final Property<String> depfile
    @Optional @Input final Property<EnumDeps> deps
    @Optional @Input final Property<String> msvc_deps_prefix
    @Optional @Input final Property<String> description
    @Optional @Input final Property<Boolean> generator
    @Optional @Input final Property<Boolean> restat
    @Optional @Input final Property<String> rspfile
    @Optional @Input final Property<String> rspfile_content
    @Optional @Input final Property<String> pool
    @Optional @Input final SetProperty<VariableDeclaration> variable_overrides

    private final ObjectFactory objects

    @Inject
    RuleDeclaration(ObjectFactory objects, String rulename) {
        this.objects = objects
        this.rulename = new DefaultProviderFactory().provider{rulename}
        this.command = objects.property(String)
        this.depfile = objects.property(String)
        this.deps = objects.property(EnumDeps)
        this.msvc_deps_prefix = objects.property(String)
        this.description = objects.property(String)
        this.generator = objects.property(Boolean)
        this.restat = objects.property(Boolean)
        this.rspfile = objects.property(String)
        this.rspfile_content = objects.property(String)
        this.pool = objects.property(String)
        this.variable_overrides = objects.setProperty(VariableDeclaration)
    }

    String getName() {
        return rulename.get()
    }

    void setCommand(String value) {
        command.set(value)
    }

    void setDepfile(String value) {
        depfile.set(value)
    }

    void setDeps(EnumDeps value) {
        deps.set(value)
    }

    void setDeps(String value) {
        setDeps(EnumDeps.valueOf(value))
    }

    void setMsvc_deps_prefix(String value) {
        msvc_deps_prefix.set(value)
    }

    void setDescription(String value) {
        description.set(value)
    }

    void setGenerator(boolean value) {
        generator.set(value)
    }

    void setRestat(Boolean value) {
        restat.set(value)
    }

    void setRspfile(String value) {
        rspfile.set(value)
    }

    void setRspfile_content(String value) {
        rspfile_content.set(value)
    }

    void setPool(String value) {
        pool.set(value)
    }

    void setVariable_overrides(Iterable<VariableDeclaration> value) {
        variable_overrides.set(value)
    }

    SetProperty<VariableDeclaration> getVariable_overrides() {
        return variable_overrides
    }

    @Override
    List<String> parse() {
        List<VariableDeclaration> vars = new ArrayList<>()
        if (command.present) {
            vars.add(new VariableDeclaration(objects, "command", command.get()))
        } else {
            throw new RuntimeException()
        }
        if (depfile.present) {
            vars.add(new VariableDeclaration(objects, "depfile", depfile.get()))
        }
        if (deps.present) {
            vars.add(new VariableDeclaration(objects, "deps", deps.get().name()))
        }
        if (msvc_deps_prefix.present) {
            vars.add(new VariableDeclaration(objects, "msvc_deps_prefix", msvc_deps_prefix.get()))
        }
        if (description.present) {
            vars.add(new VariableDeclaration(objects, "description", description.get()))
        }
        if (generator.present && generator.get()) {
            vars.add(new VariableDeclaration(objects, "generator", "1"))
        }
        if (restat.present && restat.get()) {
            vars.add(new VariableDeclaration(objects, "restat", "1"))
        }
        if (rspfile.present) {
            vars.add(new VariableDeclaration(objects, "rspfile", rspfile.get()))
        }
        if (rspfile_content.present) {
            vars.add(new VariableDeclaration(objects, "rspfile_content", rspfile_content.get()))
        }
        if (pool.present) {
            vars.add(new VariableDeclaration(objects, "pool", pool.get()))
        }
        variable_overrides.get().forEach { vars.add(it) }
        List<String> ret = new ArrayList<>()
        ret.add("rule "+rulename.get())
        vars.forEach { ret.add("  "+it.parse().get(0)) }
        return ret
    }

    enum EnumDeps {gcc, msvc}

    @Override
    boolean equals(Object o) {
        if (o instanceof RuleDeclaration) {
            return name.equals(o.name)
        } else return false
    }
}
