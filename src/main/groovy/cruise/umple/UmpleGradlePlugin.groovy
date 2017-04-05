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

    // Used to create sourceDirectorySets
    private final SourceDirectorySetFactory sourceDirectorySetFactory;

    @Inject
    UmpleGradlePlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.sourceDirectorySetFactory = sourceDirectorySetFactory
    }

    @Override
    void apply(final Project project) {
        //println("Inside apply")
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
		//TODO change bb2 back to sourceSet once we figure out the sourceSet proeprty problem
        // So now we have to go through and add the properties that we want
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all { sourceSet ->
            // For each sourceSet we're enacting an action on each one that adds an umple task to it

            // an Action is something like doLast. Usually associated with a Task. Maybe here we're just doing something (an Action) to each bb2?     

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

            //println "Sources : " +bb2.allSource.files // we've correctly added the .ump files for the main SS at this point

            // ignore the sources in the resources folder
            sourceSet.resources.filter.exclude { element -> umpleDirectorySet.contains element.file }

            addAndConfigureUmpleGenerate(project, sourceSet, umpleSourceSet)

        }
    }

    // Configures the "compileUmple*" tasks to build the umple files
    private static void addAndConfigureUmpleGenerate(final Project project,
                                                     final SourceSet sourceSet,
                                                     final UmpleSourceSet umpleSourceSet) {
        String taskName = sourceSet.getCompileTaskName("umple")
        println("configuring task: " + taskName)

        // When we get a new sourceSet, per [sub-]project, we create a "compileUmpleTask" that consists of building
        // a configuration per source set

        // Try to find the task, see if it exists
        println("Adding generate task: ${umpleSourceSet}")
        UmpleGenerateTask umpleGenerate = project.tasks.create(taskName, UmpleGenerateTask.class)

        umpleGenerate.description = "Compiles the " + sourceSet + "."
        umpleGenerate.setSourceSet(sourceSet) // the source set must be configured when UmpleGenerateTask is run so that compileSourceSetJava works properly
        umpleGenerate.source = umpleSourceSet.umple //source directory for the compileUmple task is the SourceDirectorySet in DefaultUmpleSourceSet
        umpleGenerate.compileConfig = umpleSourceSet // Now we add a configuration to the task

        if (umpleGenerate.compileConfig.language.contains(UmpleLanguage.JAVA)) {
            // TODO Add flag to turn this on/off
            project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn umpleGenerate
        }

    }
}