package cruise.umple.tasks

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain
import cruise.umple.UmpleLanguage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import java.nio.file.Paths
/**
 * Created by kevin on 15/03/2017.
 */
class UmpleGenerateTask extends SourceTask {

    def List<UmpleOptions> compileConfigs

    private UmpleSourceSet sourceSet

    UmpleGenerateTask() {
        compileConfigs = new ArrayList<>()
    }

    private void loadGlobals(UmpleOptions opts) {

        // get defaults defined in UmpleOptions.groovy
        UmpleOptions globals = (UmpleOptions)(getProject().getExtensions().getByName(UmpleOptions.NAME))
        println("Setting defaults for UmpleGenerateTask")

        if (!opts.language) {
            opts.language = globals.language
        }

        if (!opts.master) {
            opts.master = globals.master
        }

        if (!opts.output) {
            opts.output = globals.output
        }
    }

    void executeFor(UmpleOptions opts) {
        // currently we can't do incremental builds

        println("languages = " + opts.language)

        // The user specifies paths relative to the main project directory, but
        // to ensure correctness we need to use absolute paths internally

        for (UmpleLanguage language: opts.language) {

            List<File> masters = opts.master.collect { path -> Paths.get(project.path, path.toString()).toFile() }
            UmpleConsoleConfig consoleConfig = new UmpleConsoleConfig(masters.get(0).toString())

            if (masters.size() > 1) {
                opts.master.forEach{ f -> consoleConfig.addLinkedFile(f.toString()) }
            }

            consoleConfig.generate = language.toString().toLowerCase()
            consoleConfig.path = Paths.get(project.buildDir.path, opts.output.toString()).toString()

            UmpleConsoleMain consoleMain = new UmpleConsoleMain(consoleConfig)
            consoleMain.runConsole()

            // Add generated files to the generatedSource source set as source files

            if (language == UmpleLanguage.JAVA) {
                sourceSet.java.srcDir consoleConfig.path

                project.tasks.getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME).dependsOn this
            }
        }
    }

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        println("Compiling for " + sourceSet)
        compileConfigs.collect{ config -> this.executeFor(config) }
    }
}
