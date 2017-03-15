package cruise.umple.internal.tasks

import cruise.umple.tasks.UmpleSourceSet
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory

import static org.gradle.util.ConfigureUtil.configure
/**
 * Default implemenation of {@link UmpleSourceSet}
 */
class DefaultUmpleSourceSet implements UmpleSourceSet {

    private SourceDirectorySet umple;

    DefaultUmpleSourceSet(String name, SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.umple = sourceDirectorySetFactory.create(name + " Umple Source")
        umple.include "**/*.ump" // TODO static field
    }

    @Override
    SourceDirectorySet getUmple() {
        umple
    }

    @Override
    UmpleSourceSet umple(Closure configureClosure) {
        configure(configureClosure, umple)
        this
    }

    @Override
    DefaultUmpleSourceSet umple(Action<? extends SourceDirectorySet> configureAction) {
        configureAction.execute(getUmple())
        this
    }


}
