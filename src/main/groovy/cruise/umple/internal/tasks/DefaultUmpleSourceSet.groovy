package cruise.umple.internal.tasks

import cruise.umple.tasks.*
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory

import static org.gradle.util.ConfigureUtil.configure
import static com.google.common.base.Preconditions.checkNotNull
import java.nio.file.Paths
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
    
    @Override //TODO refactor. When we set a new master file we must update srcDir to make sure incremental bulds work properly
    void setMaster(File master) {
        this.master = [checkNotNull(master, "master == null")]
        println("Updating srcDir in DefaultUmpleSource: " +  master.getParentFile().getPath())
        umple.srcDir master.getParentFile() 
    }

    @Override
    UmpleSourceSet umple(Closure configureClosure) {
        println("processing the umple{} closure from the build file")      
        configure(configureClosure, umple)
        this
    }

    @Override //TODO delete this? Never used?
    UmpleSourceSet umple(Action<? extends UmpleOptions> configureAction) {
        println("processing the umple{} closure from umple(Action<UmpleOptions> configureAction)")
        configureAction.execute(this)
        this
    }
}
