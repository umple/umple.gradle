package cruise.umple

import cruise.umple.internal.tasks.DefaultUmpleOptions
import cruise.umple.internal.tasks.DefaultUmpleSourceSet
import cruise.umple.tasks.UmpleGenerateTask
import cruise.umple.tasks.UmpleSourceSet
import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject

class UmpleGradlePlugin implements Plugin<Project> {

    static final String UMPLE_CONFIGURATION_NAME = "umple"
    private final SourceDirectorySetFactory sourceDirectorySetFactory;

    @Inject
    UmpleGradlePlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.sourceDirectorySetFactory = sourceDirectorySetFactory
    }

    @Override
    void apply(final Project project) {
        // We use sourceSets because it's convenient
        project.getPluginManager().apply(JavaBasePlugin)

        // Create the default umple closure
        project.extensions.add("umple", DefaultUmpleOptions)

        final Configuration umpleConfig = project.configurations.create(UMPLE_CONFIGURATION_NAME)
            .setVisible(false)
            .setDescription("Umple library configuration for use")

        umpleConfig.defaultDependencies {
            dependencies.add(project.dependencies.create('libs/umple-latest.jar'))
        }

        // So now we have to go through and add the properties that we want
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all { sourceSet ->
            // For each sourceSet we're enacting an action on each one that adds an umple task to it

            // Get the convention and add the properties
            Convention sourceSetConvention = (Convention) InvokerHelper.getProperty(sourceSet, "convention")

            // Create the Umple closure within the source set, e.g. main { umple{} }
            DefaultUmpleSourceSet umpleSourceSet = new DefaultUmpleSourceSet(sourceSet.name, sourceDirectorySetFactory)
            sourceSetConvention.plugins.put("umple", umpleSourceSet)

            // get the source directory set from the  Umple source set so we can modify it
            final SourceDirectorySet umpleDirectorySet = umpleSourceSet.umple
            // set the name of the directory to be src/SOURCE SET NAME/umple, which is our convention
            // TODO address this convention?
            // Tell Gradle where to look for umple files when the compileUmple task is invoked. 
            // Used to check for incremental builds (compileUmple only runs if the files in this folder have changed).
            // This location is updated if the user specifies a custom path to the master.ump file (we set srcDir to the location of the master.ump file)
            umpleDirectorySet.srcDir { project.file("src/" + sourceSet.getName() + "/umple") } 

            // Add the source to all of the required sources
            sourceSet.allSource.source umpleDirectorySet

            // ignore the sources in the resources folder
            sourceSet.resources.filter.exclude { element -> umpleDirectorySet.contains element.file }
            addAndConfigureUmpleGenerate(project, sourceSet, umpleSourceSet)

        }
    }

    // Configures the "compileUmple*" tasks to build the umple files
    private static void addAndConfigureUmpleGenerate(final Project project,
                                                     final SourceSet sourceSet,
                                                     final DefaultUmpleSourceSet umpleSourceSet) {
        String taskName = sourceSet.getCompileTaskName("umple")

        // When we get a new sourceSet, per [sub-]project, we create a "compileUmpleTask" that consists of building
        // a configuration per source set

        // Try to find the task, see if it exists
        UmpleGenerateTask umpleGenerate = project.tasks.create(taskName, UmpleGenerateTask.class)

        umpleGenerate.description = "Compiles the " + sourceSet + "."
        umpleGenerate.source = umpleSourceSet.umple //source directory for the compileUmple task is the SourceDirectorySet in DefaultUmpleSourceSet
        umpleGenerate.compileConfig = umpleSourceSet // Now we add a configuration to the task

        //TODO refactor this into a seperate method
         project.afterEvaluate { // we execute everything in the closure *after* the configuration phase is complete
                // get defaults user specified within the non-source set umple closure
            //println("Configuration complete! Executing the afterEvaluate closure")
            DefaultUmpleOptions globals = (DefaultUmpleOptions)(project.extensions.getByName("umple"))
    
            // make a defensive copy so we don't change the underlying stored reference
            DefaultUmpleOptions out = new DefaultUmpleOptions()
            if (umpleSourceSet.language.isEmpty()) { // if the use hasn't specified a SourceSet-specific override (e.g. sourceSets{ main{ } })
                if (!globals.language) // if the user hasn't specified anything configuration values, use our defaults from UmpleOptions
                    out.language = globals.DEFAULT_LANGUAGE_TO_GENERATE
                else  // if the user has specified defaults using an umple closure, use them
                    out.language = globals.language
            } else {
                out.language = umpleSourceSet.language
            }
    
            if (umpleSourceSet.master.isEmpty()) {
                if (!globals.master)
                    out.master = globals.DEFAULT_MASTER_FILE
                else
                    out.master = globals.master
            } else {
                out.master = umpleSourceSet.master
            }
    
            if (!umpleSourceSet.outputDir) {    
                if (!globals.outputDir)
                    out.outputDir = globals.DEFAULT_GENERATED_OUTPUT
                else
                    out.outputDir = globals.outputDir
            } else {
                out.outputDir = umpleSourceSet.outputDir
            }
            
            if (!umpleSourceSet.dependsFlag) {    
                if (!globals.dependsFlag)
                    out.dependsFlag = globals.DEFAULT_DEPENDS_FLAG
                else
                    out.dependsFlag = globals.dependsFlag
            } else {
                out.dependsFlag = umpleSourceSet.dependsFlag
            }
            
	        if (out.language.contains(UmpleLanguage.JAVA) && out.dependsFlag) 
	        {      
	            project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn umpleGenerate	            
	            sourceSet.java.srcDir out.outputDir.toString()
	            
	        }
           
            umpleGenerate.setCompileConfig(out)
            //println (out)
        }

        // TODO Should this be static here? It feels very wrong.
        umpleGenerate.sourceRoot = project.projectDir.toPath().resolve("src/${sourceSet.name}/umple").toFile()

        project.tasks.getByName("build").dependsOn umpleGenerate

    }
}