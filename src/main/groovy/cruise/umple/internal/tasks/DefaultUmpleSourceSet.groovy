package cruise.umple.internal.tasks

import cruise.umple.tasks.UmpleOptions
import cruise.umple.tasks.UmpleSourceSet
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory

import static com.google.common.base.Preconditions.checkNotNull
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
    
    @Override //TODO refactor. When we set a new master file we must update srcDir to make sure incremental bulds work properly
    void setMaster(File master) {
        this.master = [checkNotNull(master, "master == null")]
        umple.srcDir master.parentFile
    }
    
    @Override //TODO add tests for builds where we specify multiple master file and/or multiple languages
    void setMaster(List<File> masters) {
        for (File master : masters) 
        {
            this.master = [checkNotNull(master, "master == null")]
            umple.srcDir master.parentFile
        }
    }

    @Override
    UmpleSourceSet umple(Closure configureClosure) {
        configure(configureClosure, this)
        this
    }
}
