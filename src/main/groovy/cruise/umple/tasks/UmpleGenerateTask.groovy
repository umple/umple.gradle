package cruise.umple.tasks

import cruise.umple.UmpleConsoleConfig
import cruise.umple.UmpleConsoleMain
import cruise.umple.UmpleLanguage
import cruise.umple.internal.tasks.DefaultUmpleOptions
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

import static com.google.common.base.Preconditions.checkNotNull
/**
 * Created by kevin on 15/03/2017.
 */
class UmpleGenerateTask extends SourceTask {

    private UmpleOptions compileConfig //this contains the DefaultUmpleOption we create within the apply method in UmpleGradlePlugin

    UmpleGenerateTask() {
        compileConfig = (DefaultUmpleOptions)(project.extensions.getByName(UmpleOptions.NAME))
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
        UmpleOptions opts = compileConfig
        //println(opts)

        // The user specifies paths relative to the main project directory, but
        // the umple compiler requires paths relative to CWD /or/ absolute paths, we use absolutes
        for (UmpleLanguage language: opts.language) {
            List<File> masters = opts.master

            UmpleConsoleConfig consoleConfig = new UmpleConsoleConfig(masters.get(0).toString())

            if (masters.size() > 1) {
                // Add the rest of the files that is not the master
                masters.drop(1).forEach{ f -> consoleConfig.addLinkedFile(f.toString()) }
            }

            consoleConfig.generate = language.toString().toLowerCase()

            // The output path is relative to the project, though.
            final projectPath = project.projectDir.toPath()
                    
            consoleConfig.path = projectPath.resolve(opts.resolveOutputDir(language).toPath()).toString()
            //println("The output path we use: " + consoleConfig.path.get())
            UmpleConsoleMain consoleMain = new UmpleConsoleMain(consoleConfig)
            consoleMain.runConsole()
        }
    }
}
