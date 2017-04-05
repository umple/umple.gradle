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

        UmpleOptions opts = getCompileConfig()
//        println("languages = " + opts.language)
//        println("master files = " + opts.master)
//        println("output path = " + opts.outputDir)


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
