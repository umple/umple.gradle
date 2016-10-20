package cruise.umple

import cruise.umple.UmpleConsoleConfig

import org.gradle.api.Plugin
import org.gradle.api.Project

class UmplePlugin implements Plugin<Project> {

    private UmpleConsoleConfig cfg

    @Override
    void apply(final Project project) {


        project.task('hello') << {
            println "Hello, World!"
        }
    }
}