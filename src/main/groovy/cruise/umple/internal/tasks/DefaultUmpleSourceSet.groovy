package cruise.umple.internal.tasks

import cruise.umple.tasks.*
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory

import static org.gradle.util.ConfigureUtil.configure
/**
 * Default implemenation of {@link UmpleSourceSet}
 */
class DefaultUmpleSourceSet implements UmpleSourceSet {

    private SourceDirectorySet umple;
    private UmpleGenerateTask genTask;
    File umpleFilePath;

    DefaultUmpleSourceSet(String name, SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.umple = sourceDirectorySetFactory.create(name + " Umple Source")
        umple.include "**/*.ump" // TODO static field
    }

    void setUmpleGenerateTask(UmpleGenerateTask genTask) {
        this.genTask = genTask
    }

    @Override
    SourceDirectorySet getUmple() {
        umple
    }

    @Override
    UmpleSourceSet umple(Closure configureClosure) {
        println("processing the umple{} closure from the build file")      
       
        configure(configureClosure, umple)
        genTask.setUmpleFile umpleFilePath
        //umple.srcDir 'C:/Users/i_am_/workspace/Umple Gradle Test/sub2/src/custom/umple' //TODO replace this with the absolute path on your machine, or get it from the project
       
        this // Return the DefaultUmpleSourceSet associated with this call to umple
    }

    @Override
    UmpleSourceSet umple(Action<UmpleOptions> configureAction) {
        configureAction.execute(getUmple())
        this
    }


}
