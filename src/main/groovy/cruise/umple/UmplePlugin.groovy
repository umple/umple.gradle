package cruise.umple

import cruise.umple.UmpleConsoleConfig

import org.gradle.api.Plugin
import org.gradle.api.Project

class UmplePlugin implements Plugin<Project> {

    private UmpleConsoleConfig cfg

    @Override
    void apply(final Project project) {

        project.task('compileUmpleFile') << {
			UmpleConsoleMain(cfg)
		
			if(project.hasProperty("umpleArgs"))
			{
				// arguments are specified through gradle by -P, separated by commas (-P is for project properties)
				// eg: "gradle compileUmpleFile 'P-umpleArgs=test.ump,-g,Java'"
				args(umpleArgs.split(','))
				
				UmpleConsoleMain(args)
				runConsole()
			}
			else
			{
				println "Error: Command line arguments are required to compile an Umple file"
			}
        }
    }
}