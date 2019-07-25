package tk.rdvdev2.ninjaplugin.ninja.declarations

import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class BuildDeclaration implements NinjaDeclaration {
    @Input final Property<String> rulename
    @Input final Property<FileCollection> outputs
    @Optional @Input final Property<FileCollection> implicit_outputs
    @Input final Property<FileCollection> inputs
    @Optional @Input final Property<FileCollection> implicit_dependencies
    @Optional @Input final Property<FileCollection> order_dependencies
    @Optional @Input final Property<String> pool
    @Optional @Input final SetProperty<VariableDeclaration> variable_overrides

    private final ObjectFactory objects

    BuildDeclaration(ObjectFactory objects) {
        this.objects = objects
        this.rulename = objects.property(String)
        this.outputs = objects.property(FileCollection).convention(objects.fileCollection())
        this.implicit_outputs = objects.property(FileCollection).convention(objects.fileCollection())
        this.inputs = objects.property(FileCollection).convention(objects.fileCollection())
        this.implicit_dependencies = objects.property(FileCollection).convention(objects.fileCollection())
        this.order_dependencies = objects.property(FileCollection).convention(objects.fileCollection())
        this.pool = objects.property(String)
        this.variable_overrides = objects.setProperty(VariableDeclaration)
    }

    void setRulename(String value) {
        rulename.set(value)
    }

    void setOutputs(FileCollection value) {
        outputs.set(value)
    }

    void setImplicit_outputs(FileCollection value) {
        implicit_outputs.set(value)
    }

    void setInputs(FileCollection value) {
        inputs.set(value)
    }

    void setImplicit_dependencies(FileCollection value) {
        implicit_dependencies.set(value)
    }

    void setOrder_dependencies(FileCollection value) {
        order_dependencies.set(value)
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
        String declaration = "build"
        if (!outputs.get().empty) {
            outputs.get().forEach{declaration += " "+Helpers.parsePath(it.path)}
        } else throw new RuntimeException('Missing outputs')
        if (!implicit_outputs.get().empty) {
            declaration += " |"
            implicit_outputs.get().forEach{declaration += " "+Helpers.parsePath(it.path)}
        }
        if (rulename.present) {
            declaration += ": "+rulename.get()
        } else throw new RuntimeException('Missing rulename')
        if (!inputs.get().empty) {
            inputs.get().forEach{declaration += " "+Helpers.parsePath(it.path)}
        }
        if (!implicit_dependencies.get().empty) {
            declaration += " |"
            implicit_dependencies.get().forEach{declaration += " "+Helpers.parsePath(it.path)}
        }
        if (!order_dependencies.get().empty) {
            declaration += " ||"
            order_dependencies.get().forEach{declaration += " "+Helpers.parsePath(it.path)}
        }
        if (pool.present) variable_overrides.add(new VariableDeclaration(objects, "pool", pool.get()))
        List<String> ret = new ArrayList<>(variable_overrides.get().size()+1)
        ret.add(declaration)
        variable_overrides.get().forEach { ret.add("  "+it.parse().get(0)) }
        return ret
    }
}
