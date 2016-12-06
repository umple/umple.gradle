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
	private String m_defaultOutputPath = "umpleOutput/"
	
	private UmpleConsoleConfig m_consoleConfig 
	private UmpleConsoleMain m_consoleMain

    @Override
    void apply(final Project project) {
	
		project.task('compileUmpleFile') << {
			// command line arguments are specified through gradle by -P (-P is for project properties)
			// eg: "gradle compileUmpleFile -PumpleFileName=test.ump -PlanguageToGenerate=Java"	
		
			if(project.hasProperty('umpleFileName'))
			{		
				m_umpleFileName = project.getProperty('umpleFileName')
				m_consoleConfig = new UmpleConsoleConfig(m_umpleFileName) 
			}
			else
			{
				throw new GradleException("Error: You must specify an Umple file")
			}
			
			if(project.hasProperty('languageToGenerate'))
			{				
				m_languageToGenerate = project.getProperty('languageToGenerate')
				m_consoleConfig.setGenerate(m_languageToGenerate)
			}
			else
			{
				if(m_languageToGenerate != "")
				{
					m_consoleConfig.setGenerate(m_languageToGenerate)
				}
				else
				{
					throw new GradleException("Error: You must specify a language to generate code for")
				}
			}
			
			if(project.hasProperty('outputPath'))
			{				
				m_consoleConfig.setPath(project.getProperty('outputPath'))
			}
			else
			{		
				m_consoleConfig.setPath(m_defaultOutputPath)
			}				
				
			m_consoleMain = new UmpleConsoleMain(m_consoleConfig)
				
			m_consoleMain.runConsole()
		}
		
    }
}