package cruise.umple

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException

class UmpleGradlePlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {

        project.task('compileUmpleFile') << {
			// UmpleConsoleMain(cfg)
		
			if(project.hasProperty('umpleArgs'))
			{
				// arguments are specified through gradle by -P, separated by commas (-P is for project properties)
				// eg: "gradle compileUmpleFile -PumpleArgs=test.ump,-g,Java"	
				def cfg = new UmpleConsoleConfig("test.ump") // umpleArgs.split(','))
				cfg.setGenerate("Java")
				cfg.setPath("../../libs/")
				def console = new UmpleConsoleMain(cfg)
				console.runConsole()
			}
			else
			{
				throw new GradleException("Error: Command line arguments are required to compile an Umple file")
			}
        }
    }
}