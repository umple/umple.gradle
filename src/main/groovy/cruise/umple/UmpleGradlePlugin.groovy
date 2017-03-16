package cruise.umple

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.GradleException

class UmpleGradlePlugin implements Plugin<Project> {
    // Project properties
    private static final String UMPLE_FILE_PATH = 'umpleFilePath'
    private static final String LANGUAGE_TO_GENERATE = 'languageToGenerate'
    private static final String GENERATED_OUTPUT_PATH = 'outputPath'
    
    // Default project properties. Paths are relative to location of the project's build.gradle file
    private static final String DEFAULT_LANGUAGE_TO_GENERATE = 'Java'
    private static final String DEFAULT_GENERATED_OUTPUT_PATH = "generated/java"
    private static final String DEFAULT_UMPLE_FILE_PATH = "src/umple/master.ump"

    // Member variables
    private String m_languageToGenerate 
    private String m_generatedOutputPath
    private String m_umpleFilePath
    private SourceSet m_generatedSourceSS
    private UmpleConsoleConfig m_consoleConfig 
    private UmpleConsoleMain m_consoleMain

    @Override
    void apply(final Project project) { 
        project.task('generateSource') << {     
            // The user specifies paths relative to the main project directory, but
            // to ensure correctness we need to use absolute paths internally  
            m_umpleFilePath = "${project.projectDir}/" 
            m_generatedOutputPath = "${project.projectDir}/"
            
            if(project.sourceSets.findByName("generatedSource") == null) {
                throw new GradleException("Error: You must declare a generatedSource source set in your project's build.gradle file. See the readme for details")
            }
            
            m_generatedSourceSS = project.sourceSets.generatedSource

            if(m_generatedSourceSS.hasProperty(UMPLE_FILE_PATH))
            {
                m_umpleFilePath += m_generatedSourceSS.getProperty(UMPLE_FILE_PATH) 
            } else {
                m_umpleFilePath += DEFAULT_UMPLE_FILE_PATH
            }
            m_consoleConfig = new UmpleConsoleConfig(m_umpleFilePath) 
            
            if(m_generatedSourceSS.hasProperty(LANGUAGE_TO_GENERATE))
            {   
                m_languageToGenerate = m_generatedSourceSS.getProperty(LANGUAGE_TO_GENERATE)                
            } else {
                m_languageToGenerate = DEFAULT_LANGUAGE_TO_GENERATE
            }
            m_consoleConfig.setGenerate(m_languageToGenerate)
            
            if(m_generatedSourceSS.hasProperty(GENERATED_OUTPUT_PATH))
            {
                m_generatedOutputPath += m_generatedSourceSS.getProperty(GENERATED_OUTPUT_PATH)
            } else {
                m_generatedOutputPath += DEFAULT_GENERATED_OUTPUT_PATH;
            }
            m_consoleConfig.setPath(m_generatedOutputPath)  
            
            m_consoleMain = new UmpleConsoleMain(m_consoleConfig)
            m_consoleMain.runConsole()
            
            // Add generated files to the generatedSource source set as source files
            m_generatedSourceSS.java.srcDir m_generatedOutputPath
        }    
    }    
}