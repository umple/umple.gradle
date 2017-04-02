package cruise.umple.internal.tasks

import cruise.umple.tasks.*
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory

import static org.gradle.util.ConfigureUtil.configure
/**
 * Default implemenation of {@link UmpleSourceSet}
 */
class DefaultUmpleSourceSet extends DefaultUmpleOptions implements UmpleSourceSet {

    private SourceDirectorySet umple

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
        println("processing the umple{} closure from the build file")      
       
        configure(configureClosure, umple)

        this
    }

    @Override
    UmpleSourceSet umple(Action<? extends UmpleOptions> configureAction) {
        println("processing the umple{} closure from umple(Action<UmpleOptions> configureAction)")
        configureAction.execute(this)
        this
    }
}
