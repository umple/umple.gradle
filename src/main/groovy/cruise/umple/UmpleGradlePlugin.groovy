package cruise.umple

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException

class UmpleGradlePlugin implements Plugin<Project> {

	private UmpleConsoleConfig consoleConfig 
	private UmpleConsoleMain consoleMain

    @Override
    void apply(final Project project) {

        project.task('compileUmpleFile') << {
			// UmpleConsoleMain(cfg)
		
			if(project.hasProperty('umpleArgs'))
			{
				// arguments are specified through gradle by -P, separated by commas (-P is for project properties)
				// eg: "gradle compileUmpleFile -PumpleArgs=test.ump,-g,Java"	
				
				// #TODO_AH figure out how to parse: umpleArgs.split(','))
				
				consoleConfig = new UmpleConsoleConfig("test.ump") 
				consoleConfig.setGenerate("Java")
				consoleConfig.setPath("../../libs/")
				
				consoleMain = new UmpleConsoleMain(consoleConfig)
				
				consoleMain.runConsole()
			}
			else
			{
				throw new GradleException("Error: Command line arguments are required to compile an Umple file")
			}
        }
    }
}