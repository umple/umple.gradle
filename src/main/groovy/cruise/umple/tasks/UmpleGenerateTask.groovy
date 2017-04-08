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

    @Input
    def File sourceRoot //TODO refactor. We need access to the source set, but this may not be the best way to do it.

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
        // to umple requires paths relative to CWD /or/ absolute paths, absolute
        // are much easier


        // we use the following paths when doing resolving
        final projectPath = project.projectDir.toPath()
        final sourceSetPath = sourceRoot.toPath()

        for (UmpleLanguage language: opts.language) {

            // path iterates over the Files in master
            List<File> masters = opts.master.collect { path ->
                // The users specifies file that are relative to the sourceSet, thus we resolve the path against
                // the "projectPath"
                final projRel = projectPath.relativize(path.toPath())

                // What we really want is the path resolved against the sourceSet though, so we do that by resolving
                // against the path of the sourceSet
                sourceSetPath.resolve(projRel).toFile()
            }

            UmpleConsoleConfig consoleConfig = new UmpleConsoleConfig(masters.get(0).toString())

            if (masters.size() > 1) {
                // Add the rest of the files that is not the master
                opts.master.drop(1).forEach{ f -> consoleConfig.addLinkedFile(f.toString()) }
            }

            consoleConfig.generate = language.toString().toLowerCase()

            // The output path is relative to the project, though.
            consoleConfig.path = projectPath.resolve(opts.resolveOutputDir(language).toPath()).toString()

            UmpleConsoleMain consoleMain = new UmpleConsoleMain(consoleConfig)
            consoleMain.runConsole()
        }
    }
}
