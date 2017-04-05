package cruise.umple.tasks

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain
import cruise.umple.UmpleLanguage
import cruise.umple.internal.tasks.DefaultUmpleOptions
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import java.nio.file.Paths

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Created by kevin on 15/03/2017.
 */
class UmpleGenerateTask extends SourceTask {

    private UmpleOptions compileConfig //this contains the DefaultUmpleOption we create within the apply method in UmpleGradlePlugin

    private UmpleSourceSet umpleSourceSet

    @Input
    def SourceSet sourceSet //TODO refactor. We need access to the source set, but this may not be the best way to do it.

    UmpleGenerateTask() {
        compileConfig = (DefaultUmpleOptions)(project.extensions.getByName(UmpleOptions.NAME))
    }


    UmpleOptions getCompileConfig() {

        // get defaults user specified within the non-source set umple closure
        DefaultUmpleOptions globals = (DefaultUmpleOptions)(project.extensions.getByName(UmpleOptions.NAME))
        println("Setting configuration for UmpleGenerateTask: Extension for project: ${project} here's the umple options ${project.umple}")

        // make a defensive copy so we don't change the underlying stored reference
        UmpleOptions out = new DefaultUmpleOptions()

        if (!compileConfig.language) { // if the use hasn't specified a SourceSet-specific override (e.g. sourceSets{ main{ } })
            if (!globals.language) // if the user hasn't specified anything configuration values, use our defaults from UmpleOptions
                out.language = globals.DEFAULT_LANGUAGE_TO_GENERATE
            else  // if the user has specified defaults using an umple closure, use them
                out.language = globals.language
        } else {
            out.language = compileConfig.language
        }

        if (!compileConfig.master) {
            if (!globals.master)
                out.master = globals.DEFAULT_MASTER_FILE
            else
                out.master = globals.master
        } else {
            out.master = compileConfig.master
        }

        if (!compileConfig.outputDir) {

            if (!globals.outputDir)
                out.outputDir = globals.DEFAULT_GENERATED_OUTPUT
            else
                out.outputDir = globals.outputDir
        } else {
            out.outputDir = compileConfig.outputDir
        }

        out
    }

    // updates the umple generate task's configuration. If any configuration values have been specified by the user, we use those. Otherwise we use the
    // defaults from UmptionOptions
    @Input
    UmpleOptions setCompileConfig(UmpleOptions opts) {
        UmpleOptions old = this.compileConfig
        this.compileConfig = checkNotNull(opts, "opts == null")
        old
    }

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        println("languages = " + compileConfig.language)
        println("master file = " + compileConfig.master)
        println("output path = " + compileConfig.outputDir)

        // The user specifies paths relative to the main project directory, but
        // to ensure correctness we need to use absolute paths internally

        for (UmpleLanguage language: compileConfig.language) {

            // path iterates over the Files in master
            List<File> masters = compileConfig.master.collect { path -> Paths.get(path.toString()).toFile() } // the user specifes location releative to the project directory
            UmpleConsoleConfig consoleConfig = new UmpleConsoleConfig(masters.get(0).toString())

            if (masters.size() > 1) {
                compileConfig.master.forEach{ f -> consoleConfig.addLinkedFile(f.toString()) }
            }

            consoleConfig.generate = language.toString().toLowerCase()
            consoleConfig.path = Paths.get(compileConfig.outputDir.toString()).toString()

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
}
