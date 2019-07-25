package tk.rdvdev2.ninjaplugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.internal.Actions
import org.gradle.util.ConfigureUtil
import tk.rdvdev2.ninjaplugin.ninja.NinjaSourceSet

import javax.inject.Inject;

class NinjaExtension {
    final Property<String> version
    final Property<Boolean> build
    final Property<String> ninja_status
    private final NamedDomainObjectContainer<NinjaSourceSet> sourceSets

    @Inject
    NinjaExtension(Project project) {
        version = project.objects.property(String)
        version.convention("1.9.0")

        build = project.objects.property(Boolean)
        build.convention(false)

        ninja_status = project.objects.property(String)
        ninja_status.convention("[%f/%t] ")

        sourceSets = project.container(NinjaSourceSet, {return new NinjaSourceSet(project, it)} as NamedDomainObjectFactory)
        sourceSets.register('main')
    }

    NamedDomainObjectContainer<NinjaSourceSet> sourceSets(Closure closure) {
        return ConfigureUtil.configure(closure, sourceSets)
    }

    NamedDomainObjectContainer<NinjaSourceSet> sourceSets(Action<? super NamedDomainObjectContainer<NinjaSourceSet>> action) {
        return Actions.with(sourceSets, action)
    }

    protected List<NinjaSourceSet> getSourceSets() {
        return sourceSets.toList()
    }
}
