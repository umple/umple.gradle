package cruise.umple.tasks

import cruise.umple.internal.tasks.DefaultUmpleOptions
import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain
import cruise.umple.UmpleLanguage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.SourceSet
import org.gradle.api.file.SourceDirectorySet

import java.nio.file.Paths
/**
 * Created by kevin on 15/03/2017.
 */
class UmpleGenerateTask extends SourceTask {

    def List<UmpleOptions> compileConfigs //this contains the DefaultUmpleOption we create within the apply method in UmpleGradlePlugin

    private UmpleSourceSet umpleSourceSet
    private SourceSet sourceSet //TODO refactor. We need access to the source set, but this may not be the best way to do it.

    UmpleGenerateTask() {
        compileConfigs = new ArrayList<>()
    }
    
    public setSourceSet(SourceSet ss) {
        sourceSet = ss
    }

    // updates the umple generate task's configuration. If any configuration values have been specified by the user, we use those. Otherwise we use the
    // defaults from UmptionOptions
    private void setUmpleGenerateConfig(UmpleOptions opts) {

        // get defaults user specified within the non-source set umple closure
        DefaultUmpleOptions globals = (DefaultUmpleOptions)(getProject().getExtensions().getByName(UmpleOptions.NAME))
        println("Setting configuration for UmpleGenerateTask: Here my extenstion for project :" + getProject() + " here's the umple options" +  getProject().umple)

        if (!opts.language) { // if the use hasn't specified a SourceSet-specific override (e.g. sourceSets{ main{ } })
            if (!globals.language) // if the user hasn't specified anything configuration values, use our defaults from UmpleOptions
                opts.language = globals.DEFAULT_LANGUAGE_TO_GENERATE
            else  // if the user has specified defaults using an umple closure, use them
                opts.language = globals.language
        }
         
        if (!opts.master) {    
          
            if (!globals.master)
                opts.master = globals.DEFAULT_MASTER_FILE
            else 
                opts.master = globals.master

        }

        if (!opts.outputDir) {
           
            if (!globals.outputDir)
                opts.outputDir = globals.DEFAULT_GENERATED_OUTPUT
            else 
                opts.outputDir = globals.outputDir
        }
    }

    void executeFor(UmpleOptions opts) {
        // currently we can't do incremental builds

        setUmpleGenerateConfig(opts) 
        println("languages = " + opts.language)
        println("master file = " + opts.master)
        println("output path = " + opts.outputDir)

        // The user specifies paths relative to the main project directory, but
        // to ensure correctness we need to use absolute paths internally

        for (UmpleLanguage language: opts.language) {

            // path iterates over the Files in master
            List<File> masters = opts.master.collect { path -> Paths.get(path.toString()).toFile() } // the user specifes location releative to the project directory
            UmpleConsoleConfig consoleConfig = new UmpleConsoleConfig(masters.get(0).toString())

            if (masters.size() > 1) {
                opts.master.forEach{ f -> consoleConfig.addLinkedFile(f.toString()) }
            }

            consoleConfig.generate = language.toString().toLowerCase()
            consoleConfig.path = Paths.get(opts.outputDir.toString()).toString()

            UmpleConsoleMain consoleMain = new UmpleConsoleMain(consoleConfig)
            consoleMain.runConsole()

            // Add generated files to the generatedSource source set as source files
            // TODO this doesnt work because the generate task runs before the compile task. We might be able to use mustRunAfter, but I couldn't get it to work
            // Currently the user needs to set up the sourceSet stuff themselves. See TestProject's build file for an example.
            // I think this is ok. Better to let user choose if they want to compile and how they want to do it. IMO our plugin should focus on Umple stuff
            if (language == UmpleLanguage.JAVA) {
                //sourceSet.java.srcDir consoleConfig.path.get() // this should make compileSourceSetJava look in the generated folder for Java files
                //project.tasks.getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME).dependsOn this
            }
        }
    }

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        compileConfigs.collect{ config -> this.executeFor(config) }
    }
}
