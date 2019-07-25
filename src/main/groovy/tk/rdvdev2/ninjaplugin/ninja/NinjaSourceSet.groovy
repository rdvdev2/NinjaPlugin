package tk.rdvdev2.ninjaplugin.ninja

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Namer
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.provider.DefaultProviderFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.cache.internal.DefaultCrossBuildInMemoryCacheFactory
import org.gradle.internal.Actions
import org.gradle.internal.event.DefaultListenerManager
import org.gradle.internal.instantiation.DefaultInstantiatorFactory
import org.gradle.internal.instantiation.InjectAnnotationHandler
import org.gradle.internal.reflect.Instantiator
import org.gradle.util.ConfigureUtil
import tk.rdvdev2.ninjaplugin.ninja.declarations.BuildDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.CommentDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.DefaultDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.NinjaDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.PoolDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.ReferenceDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.RuleDeclaration
import tk.rdvdev2.ninjaplugin.ninja.declarations.VariableDeclaration

import javax.inject.Inject
import javax.lang.model.element.NestingKind
import java.util.function.Function

class NinjaSourceSet {

    private final Project project
    private final String name

    @InputDirectory final DirectoryProperty srcDirectory
    final DirectoryProperty buildDirectory
    @Input final Property<Boolean> includeInMaster
    @Nested final ListProperty<CommentDeclaration> comments
    @Nested final SetProperty<ReferenceDeclaration> references
    @Nested final SetProperty<String> defaultArtifacts
    private final NamedDomainObjectContainer<VariableDeclaration> variables
    private final NamedDomainObjectContainer<PoolDeclaration> pools
    private final NamedDomainObjectContainer<RuleDeclaration> rules
    @Nested final SetProperty<BuildDeclaration> buildTasks

    @Nested final Provider<Set<VariableDeclaration>> variablesProvider
    @Nested final Provider<Set<PoolDeclaration>> poolsProvider
    @Nested final Provider<Set<RuleDeclaration>> rulesProvider
    @Internal final Provider<DefaultDeclaration> defaults

    @Inject
    NinjaSourceSet(Project project, String name) {
        this.project = project
        this.name = name
        this.srcDirectory = project.objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/"+name))
        this.buildDirectory = project.objects.directoryProperty().convention(project.layout.buildDirectory.dir(name))
        this.includeInMaster = project.objects.property(Boolean).convention(true)
        this.comments = project.objects.listProperty(CommentDeclaration)
        this.references = project.objects.setProperty(ReferenceDeclaration)
        this.defaultArtifacts = project.objects.setProperty(String)
        this.variables = project.container(VariableDeclaration, {return new VariableDeclaration(project.objects, it) } as NamedDomainObjectFactory)
        this.pools = project.container(PoolDeclaration, {return new PoolDeclaration(project.objects, it)} as NamedDomainObjectFactory)
        this.rules = project.container(RuleDeclaration, {return new RuleDeclaration(project.objects, it)} as NamedDomainObjectFactory)
        this.buildTasks = project.objects.setProperty(BuildDeclaration)

        DefaultProviderFactory f = new DefaultProviderFactory()
        this.variablesProvider = f.provider{variables.toSet()}
        this.poolsProvider = f.provider{pools.toSet()}
        this.rulesProvider = f.provider{rules.toSet()}
        this.defaults = defaultArtifacts.flatMap new Transformer<Provider<? extends DefaultDeclaration>, Set<String>>() {
            @Override
            Provider<? extends DefaultDeclaration> transform(Set<String> strings) {
                def ret = new DefaultDeclaration(project.objects)
                ret.targets.addAll(strings)
                return new DefaultProviderFactory().provider{ret}
            }
        }
    }

    String getName() {
        return name
    }

    void setSrcDirectory(Directory value) {
        srcDirectory.set(value)
    }

    void setSrcDir(Directory value) {
        setSrcDirectory(value)
    }

    Directory getSrcDir() {
        return srcDirectory.get()
    }

    void setBuildDirectory(Directory value) {
        buildDirectory.set(value)
    }

    void setBuildDir(Directory value) {
        setBuildDirectory(value)
    }

    Directory getBuildDir() {
        return buildDirectory.get()
    }

    void setIncludeInMaster(boolean value) {
        includeInMaster.set(value)
    }

    void setComments(Iterable<CommentDeclaration> value) {
        comments.set(value)
    }

    ListProperty<CommentDeclaration> getComments() {
        return comments
    }

    void setReferences(Iterable<ReferenceDeclaration> value) {
        references.set(value)
    }

    SetProperty<ReferenceDeclaration> getReferences() {
        return references
    }

    void setDefaultArtifacts(Iterable<String> value) {
        defaultArtifacts.set(value)
    }

    SetProperty<String> getDefaultArtifacts() {
        return defaultArtifacts
    }

    NamedDomainObjectContainer<VariableDeclaration> variables(Closure closure) {
        return ConfigureUtil.configure(closure, variables)
    }

    NamedDomainObjectContainer<VariableDeclaration> variables(Action<? super NamedDomainObjectContainer<VariableDeclaration>> action) {
        return Actions.with(variables, action)
    }

    NamedDomainObjectContainer<PoolDeclaration> pools(Closure closure) {
        return ConfigureUtil.configure(closure, pools)
    }

    NamedDomainObjectContainer<PoolDeclaration> pools(Action<? super NamedDomainObjectContainer<PoolDeclaration>> action) {
        return Actions.with(pools, action)
    }

    NamedDomainObjectContainer<RuleDeclaration> rules(Closure closure) {
        return ConfigureUtil.configure(closure, rules)
    }

    NamedDomainObjectContainer<RuleDeclaration> rules(Action<? super NamedDomainObjectContainer<RuleDeclaration>> action) {
        return Actions.with(rules, action)
    }

    void setBuildTasks(Iterable<BuildDeclaration> value) {
        buildTasks.set(value)
    }

    SetProperty<BuildDeclaration> getBuildTasks() {
        return buildTasks
    }

    BuildDeclaration addBuild(Closure closure) {
        def build = new BuildDeclaration(project.objects)
        build = ConfigureUtil.configure(closure, build)
        buildTasks.add(build)
        return build
    }

    BuildDeclaration addBuild(Action<? super BuildDeclaration> action) {
        def build = new BuildDeclaration(project.objects)
        build = Actions.with(build, action)
        buildTasks.add(build)
        return build
    }

    void addComment(String comment) {
        comments.add(new CommentDeclaration(project.objects, comment))
    }

    void addComment(String comment, boolean prefixSpace) {
        comments.add(new CommentDeclaration(project.objects, comment, prefixSpace))
    }

    void addReference(ReferenceDeclaration.TypeEnum type, RegularFile file) {
        def declaration = new ReferenceDeclaration(project.objects)
        declaration.type = type
        declaration.file = file
        references.add(declaration)
    }

    void addReference(String type, RegularFile file) {
        addReference(ReferenceDeclaration.TypeEnum.valueOf(type), file)
    }

    List<NinjaDeclaration> getDeclarations() {
        List<NinjaDeclaration> ret = new ArrayList<>()
        ret.addAll(comments.get())
        ret.addAll(references.get())
        if(!defaults.get().targets.get().empty) ret.add(defaults.get())
        ret.addAll(variablesProvider.get())
        ret.addAll(poolsProvider.get())
        ret.addAll(rulesProvider.get())
        ret.addAll(buildTasks.get())
        return ret
    }

    @Override
    boolean equals(Object o) {
        if (o instanceof NinjaSourceSet) {
            return name.equals(o.name)
        } else return false
    }
}
