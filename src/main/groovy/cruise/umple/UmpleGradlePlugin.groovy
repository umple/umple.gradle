package cruise.umple

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException

class UmpleGradlePlugin implements Plugin<Project> {

	// Member variables
	private String m_umpleFileName = ""
	private String m_languageToGenerate = ""
	private String m_outputPath = ""
	
	private UmpleConsoleConfig consoleConfig 
	private UmpleConsoleMain consoleMain

    @Override
    void apply(final Project project) {

		project.task('compileUmpleFileToJava') <<
		{
			m_languageToGenerate = "Java"
		}
		
		project.task('compileUmpleFileToCpp') <<
		{
			m_languageToGenerate = "Cpp"
		}
		
		project.task('compileUmpleFileToSQL') <<
		{
			m_languageToGenerate = "SQL"
		}
	
        project.task('compileUmpleFile') << {
			// command line arguments are specified through gradle by -P (-P is for project properties)
			// eg: "gradle compileUmpleFile -PumpleFileName=test.ump -PlanguageToGenerate=Java"	
		
			if(project.hasProperty('umpleFileName'))
			{		
				m_umpleFileName = project.getProperty('umpleFileName')
				consoleConfig = new UmpleConsoleConfig(m_umpleFileName) 
			}
			else
			{
				throw new GradleException("Error: You must specify an Umple file")
			}
			
			if(project.hasProperty('languageToGenerate'))
			{				
				m_languageToGenerate = project.getProperty('languageToGenerate')
				consoleConfig.setGenerate(m_languageToGenerate)
			}
			else
			{
				if(m_languageToGenerate != "")
				{
					consoleConfig.setGenerate(m_languageToGenerate)
				}
				else
				{
					throw new GradleException("Error: You must specify a language to generate code for")
				}
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
		
		// #TODO_AH figure out why this doesn't work
		project.compileUmpleFile.mustRunAfter project.compileUmpleFileToJava
		project.compileUmpleFile.mustRunAfter project.compileUmpleFileToCpp
		project.compileUmpleFile.mustRunAfter project.compileUmpleFileToSQL
    }
}