package cruise.umple

import cruise.umple.internal.tasks.DefaultUmpleOptions
import cruise.umple.internal.tasks.DefaultUmpleSourceSet
import cruise.umple.tasks.UmpleGenerateTask
import cruise.umple.tasks.UmpleSourceSet
import org.codehaus.groovy.runtime.InvokerHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.plugins.DslObject;
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
        // We use sourceSets because its convenient
        project.getPluginManager().apply(JavaBasePlugin)

        project.extensions.add("umple", DefaultUmpleOptions)

        final Configuration umpleConfig = project.configurations.create(UMPLE_CONFIGURATION_NAME)
            .setVisible(false)
            .setDescription("Umple library configuration for use")

        umpleConfig.defaultDependencies {
            dependencies.add(project.dependencies.create('libs/umple-latest.jar'))
        }
		//TODO change bb2 back to sourceSet once we figure out the sourceSet proeprty problem
        // So now we have to go through and add the properties that we want
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all { bb2 ->
            // an Action is something like doLast. Usually associated with a Task. Maybe here we're just doing something (an Action) to each bb2?     
	            // Get the convention and add the properties
	            //Convention bb2Convention = (Convention) InvokerHelper.getProperty(bb2, "convention")
	
	            // We create a new umple source set
	            DefaultUmpleSourceSet umplebb2 = new DefaultUmpleSourceSet(bb2.name, sourceDirectorySetFactory)

	           //somehow associate the umple source set with the source set... maybe this is the deafult closure outside of the bb2 closure?
	            new DslObject(bb2).convention.plugins.umple =  umplebb2; //http://hamletdarcy.blogspot.ca/2010/03/gradle-plugin-conventions-groovy-magic.html
	            
	            //bb2Convention.plugins.put("umple", umplebb2)
	            // get the source directory set from the  Umple source set so we can modify it
	            final SourceDirectorySet umpleDirectorySet = umplebb2.umple
	            // set the name of the directory to be src/SOURCE SET NAME/umple, which is our convention
	            // TODO address this convention?
	            umpleDirectorySet.srcDir { project.file("src/" + bb2.getName() + "/umple") }
	            

	            // Add the source to all of the required sources
	            bb2.allSource.source umpleDirectorySet
	            
	            //println "Sources : " +bb2.allSource.files // we've correctly added the .ump files for the main SS at this point
	
	            // ignore the sources in the resources folder
	            bb2.resources.filter.exclude { element -> umpleDirectorySet.contains element.file }
	            
	            addAndConfigureUmpleGenerate(project, bb2) 
            
        }
    }
	//TODO change abc123 back to sourceSet once we figure out the source set problem
    private static void addAndConfigureUmpleGenerate(final Project project, final SourceSet abc123) {
        String taskName = abc123.getCompileTaskName("umple")
        println("configuring task: " + taskName)
        final UmpleGenerateTask umpleGenerate = project.tasks.create(taskName, UmpleGenerateTask.class)

        UmpleSourceSet umpleSourceSet =  abc123.convention.plugins.umple
        umpleSourceSet.setUmpleGenerateTask(umpleGenerate)
        
        println("umpleFilePath in addAndConfigureUmpleGenerae It's" + umpleSourceSet.umpleFilePath)
        umpleGenerate.description = "Compiles the " + umpleSourceSet.umple + "."
        umpleGenerate.source = umpleSourceSet.umple //source directory for the compileUmple task is the SourceDirectorySet in DefaultUmpleSourceSet
        project.tasks.getByName(abc123.classesTaskName).dependsOn taskName
    }
}