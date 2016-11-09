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
			// command line arguments are specified through gradle by -P (-P is for project properties)
			// eg: "gradle compileUmpleFile -PumpleFileName=test.ump -PlanguageToGenerate=Java"	
		
			if(project.hasProperty('umpleFileName'))
			{				
				consoleConfig = new UmpleConsoleConfig(project.getProperty('umpleFileName')) 
			}
			else
			{
				throw new GradleException("Error: You must specify an Umple file")
			}
			
			if(project.hasProperty('languageToGenerate'))
			{				
				consoleConfig.setGenerate(project.getProperty('languageToGenerate'))
			}
			else
			{
				throw new GradleException("Error: You must specify a language to generate code for")
			}
			
			if(project.hasProperty('outputPath'))
			{				
				consoleConfig.setPath(project.getProperty('outputPath'))
			}
			else
			{
				def defaultOutputPath = "../../libs/"
			
				consoleConfig.setPath(defaultOutputPath)
			}				
				
			consoleMain = new UmpleConsoleMain(consoleConfig)
				
			consoleMain.runConsole()
        }
    }
}