package tk.rdvdev2.ninjaplugin.ninja.declarations

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.internal.impldep.com.google.api.client.util.Sets

class DefaultDeclaration implements NinjaDeclaration {
    @Input final SetProperty<String> targets

    DefaultDeclaration(ObjectFactory objects) {
        this.targets = objects.setProperty(String).convention{Sets.newHashSet()}
    }

    void setTargets(Iterable<String> value) {
        targets.set(value)
    }

    SetProperty<String> getTargets() {
        return targets
    }

    @Override
    List<String> parse() {
        String ret = "default"
        if(!targets.get().empty) {
            targets.get().forEach{ret += " "+it}
        } else return new ArrayList<String>()
        return Arrays.asList(ret)
    }
}
