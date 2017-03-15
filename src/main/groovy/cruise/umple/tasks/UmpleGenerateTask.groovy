package cruise.umple.tasks

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain
import cruise.umple.UmpleLanguage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.*
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import java.nio.file.Paths

import static com.google.common.base.Preconditions.checkNotNull
/**
 * Created by kevin on 15/03/2017.
 */
class UmpleGenerateTask extends SourceTask {

    private UmpleLanguage m_languageToGenerate
    private File m_umpleFile
    private File m_outputRootDir

    private UmpleSourceSet m_sourceSet

    UmpleGenerateTask() {
        m_languageToGenerate = globals.languageToGenerate
        m_umpleFile = globals.umpleFilePath
    }

    private UmpleOptions getGlobals() {
        return (UmpleOptions)(getProject().getExtensions().getByName(UmpleOptions.NAME));
    }

    @Input
    UmpleSourceSet getSourceSet() {
        m_sourceSet
    }

    void setSourceSet(UmpleSourceSet set) {
        m_sourceSet = checkNotNull(set, "set == null")
    }

    @Input @Optional
    UmpleLanguage getLanguageToGenerate() {
        m_languageToGenerate
    }

    void setLanguageToGenerate(UmpleLanguage languageToGenerate) {
        checkNotNull(languageToGenerate, "languageToGenerate == null")
        this.m_languageToGenerate = languageToGenerate
    }

    @InputFile @Optional
    File getUmpleFile() {
        m_umpleFile
    }

    void setUmpleFile(File umpleFile) {
        this.m_umpleFile = umpleFile;
    }

    @OutputDirectory
    File getOutputDir() {
        Paths.get(m_outputRootDir.getPath(), m_languageToGenerate.toString().toLowerCase()).toFile()
    }

    @Input
    File getOutputRootDir() {
        return m_outputRootDir
    }

    void setOutputRootDir(File outputDir) {
        this.m_outputRootDir = outputDir
    }


    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        // currently we can't do incremental builds

        println("language = " + m_languageToGenerate)

        // The user specifies paths relative to the main project directory, but
        // to ensure correctness we need to use absolute paths internally

        UmpleConsoleConfig consoleConfig = new UmpleConsoleConfig(Paths.get(project.path, m_umpleFile.path).toString())
        consoleConfig.path = Paths.get(project.buildDir.path, outputDir.toString()).toString()

        UmpleConsoleMain consoleMain = new UmpleConsoleMain(consoleConfig)
        consoleMain.runConsole()

        // Add generated files to the generatedSource source set as source files

        if (m_languageToGenerate == UmpleLanguage.JAVA) {
            sourceSet.java.srcDir outputDir

            project.tasks.getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME).dependsOn this
        }
    }
}
