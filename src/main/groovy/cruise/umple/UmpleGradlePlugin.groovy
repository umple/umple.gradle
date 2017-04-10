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
import java.nio.file.Path

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

        project.afterEvaluate {
            // we execute everything in the closure *after* the configuration phase is complete
            //println("Configuration complete! Executing the afterEvaluate closure")

            // make a defensive copy so we don't change the underlying stored reference
            DefaultUmpleOptions out = new DefaultUmpleOptions()
            
            
            processConfiguration(out, umpleSourceSet, project, sourceSet, umpleGenerate)
           
            umpleGenerate.setCompileConfig(out)
            //println (out)
        }

        project.tasks.getByName("build").dependsOn umpleGenerate //TODO should this be enabled in all cases?
    }
        
    // Reads the configuration that has been provided by the user. SourceSet-specific configuration has priority over default configuration (either
    // the system defaults we've hard-coded into UmptionOptions.groovy or global defaults specified by the user). Global defaults
    // have priority over system defaults.
    //
    // This function takes these rules into account when populating our final configuration object, `out`
    private static void processConfiguration(DefaultUmpleOptions out, 
                                             final DefaultUmpleSourceSet umpleSourceSet,
                                             final Project project,
                                             final SourceSet sourceSet, 
                                             final UmpleGenerateTask umpleGenerate) {
        // get defaults user specified within the non-source set umple closure
        DefaultUmpleOptions globals = (DefaultUmpleOptions)(project.extensions.getByName("umple"))
    
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
    
        if (umpleSourceSet.outputDir == null) {    
            if (!globals.outputDir)
                out.outputDir = globals.DEFAULT_GENERATED_OUTPUT
            else
                out.outputDir = globals.outputDir
        } else {
            out.outputDir = umpleSourceSet.outputDir
        }
    
        if (umpleSourceSet.compileGenerated == null) {    
            if (!globals.compileGenerated)
                out.compileGenerated = globals.DEFAULT_COMPILE_GENERATED_FLAG
            else
                out.compileGenerated = globals.compileGenerated
        } else {
            out.compileGenerated = umpleSourceSet.compileGenerated
        }
        
        if (umpleSourceSet.customMasterPath == null) {    
            if (!globals.customMasterPath)
                out.customMasterPath = globals.DEFAULT_CUSTOM_MASTER_PATH_FLAG
            else
                out.customMasterPath = globals.customMasterPath
        } else {
            out.customMasterPath = umpleSourceSet.customMasterPath
        }
       
        configureMasterPaths(out, umpleSourceSet, project, sourceSet)
        
        if (out.language.contains(UmpleLanguage.JAVA) && out.compileGenerated) 
        {      
            project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn umpleGenerate
            // overwrite the existing Java srcDirs. We want Gradle to only look at the folder that contains the output of the compileUmple task            
            sourceSet.java.srcDirs = [out.outputDir.toString()]        
        }
    }


    //TODO add tests for this
    // We process paths differently depending on whether the user is following the {project dir}/src/{source set}/umple convention
    // or not. By default we assume the former.
    private static void configureMasterPaths(DefaultUmpleOptions out, 
                                             final DefaultUmpleSourceSet umpleSourceSet, 
                                             final Project project,
                                             final SourceSet sourceSet) {        
        if (out.customMasterPath) {
            // The user has specified they want to override the convention. Master paths are relative to root project dir, 
            for (File master : out.master) 
            {
                umpleSourceSet.umple.srcDir master.getParent()
            }
        } else {
            // Master paths are relative to src/{source set}/umple
            final Path conventionalMasterPath = project.file("src/" + sourceSet.getName() + "/umple").toPath()
            final projectPath = project.projectDir.toPath()
            ArrayList<File> resolvedMasters = new ArrayList();
            for (File master : out.master) 
            {
                // Get the relative path specified by the user
                final projRel = projectPath.relativize(master.toPath())
                // Append the relative path to the the fully qualified convention path
                resolvedMasters.add(conventionalMasterPath.resolve(projRel).toFile())

                umpleSourceSet.umple.srcDir master.getParent()
            }
            out.master = resolvedMasters
        }
    }
}