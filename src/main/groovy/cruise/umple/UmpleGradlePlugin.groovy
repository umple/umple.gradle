package cruise.umple

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain

import org.gradle.api.Plugin
import org.gradle.api.Project

class UmpleGradlePlugin implements Plugin<Project> {
	// Project properties
	private final String UMPLE_FILE_PATH = 'umpleFileName'
	private final String LANGUAGE_TO_GENERATE = 'languageToGenerate'
	private final String GENERATED_OUTPUT_PATH = 'outputPath'

	// Member variables
	private String m_languageToGenerate = ''
	private String m_generatedOutputPath = ''
	private m_umpleFilePath = ''
	private UmpleConsoleConfig m_consoleConfig 
	private UmpleConsoleMain m_consoleMain

	@Override
	void apply(final Project project) { 
		// Set default property values
		m_languageToGenerate = 'Java'
		m_generatedOutputPath = "${project.buildDir}${File.separator}generated${File.separator}src${File.separator}java"
		m_umpleFilePath = "${project.projectDir}${File.separator}src${File.separator}master.ump"
		
		project.task('generateSource') << {		
			// command line arguments can be specified through gradle by -P (-P is for project properties)
			// eg: "gradle compileUmpleFile -PUMPLE_FILE_PATH=test.ump -PLANGUAGE_TO_GENERATE=Java"	
		
			if(project.hasProperty(UMPLE_FILE_PATH))
			{		
				m_umpleFilePath = project.getProperty(UMPLE_FILE_PATH)	
			}
			m_consoleConfig = new UmpleConsoleConfig(m_umpleFilePath) 
			
			
			if(project.hasProperty(LANGUAGE_TO_GENERATE))
			{				
				m_languageToGenerate = project.getProperty(LANGUAGE_TO_GENERATE)				
			}
			m_consoleConfig.setGenerate(m_languageToGenerate)
			
			if(project.hasProperty(GENERATED_OUTPUT_PATH))
			{			
				m_generatedOutputPath = project.getProperty(GENERATED_OUTPUT_PATH)
			}		
			m_consoleConfig.setPath(m_generatedOutputPath)	
			
			m_consoleMain = new UmpleConsoleMain(m_consoleConfig)
			m_consoleMain.runConsole()
			
			addGeneratedToSource(project)
		}	 
	}

	void addGeneratedToSource(Project project) {
		project.sourceSets.matching { it.name == "generatedSource" } .all {
			it.java.srcDir "${project.buildDir}${File.separator}generated${File.separator}src${File.separator}java"
		}
 	}		
}