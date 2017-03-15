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
        // We use sourceSets because its convenient
        project.getPluginManager().apply(JavaBasePlugin)

        project.extensions.add("umple", DefaultUmpleOptions)

        final Configuration umpleConfig = project.configurations.create(UMPLE_CONFIGURATION_NAME)
            .setVisible(false)
            .setDescription("Umple library configuration for use")

        umpleConfig.defaultDependencies {
            dependencies.add(project.dependencies.create('libs/umple-latest.jar'))
        }

        // So now we have to go through and add the properties that we want
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all { sourceSet ->
            // Get the convention and add the properties
            Convention sourceSetConvention = (Convention) InvokerHelper.getProperty(sourceSet, "convention")

            // We create a new umple source set
            DefaultUmpleSourceSet umpleSourceSet = new DefaultUmpleSourceSet(sourceSet.name, sourceDirectorySetFactory)
            sourceSetConvention.plugins.put("umple", umpleSourceSet)
            // get the source directory set
            final SourceDirectorySet umpleDirectorySet = umpleSourceSet.umple
            // set the name of the directory to be src/NAME/umple, which is our convention
            // TODO address this convention?
            umpleDirectorySet.srcDir { project.file("src/" + sourceSet.getName() + "/umple") }

            // Add the source to all of the required sources
            sourceSet.allSource.source umpleDirectorySet

            // ignore the sources in the resources folder
            sourceSet.resources.filter.exclude { element -> umpleDirectorySet.contains element.file }

            configureUmpleGenerate(project, sourceSet)
        }
    }

    private static void configureUmpleGenerate(final Project project, final SourceSet sourceSet) {
        String taskName = sourceSet.getCompileTaskName("umple")
        final UmpleGenerateTask umpleGenerate = project.tasks.create(taskName, UmpleGenerateTask.class)

        Convention umpleConvention = (Convention) InvokerHelper.getProperty(sourceSet, "convention")
        UmpleSourceSet umpleSourceSet = umpleConvention.findPlugin(UmpleSourceSet.class)
        umpleGenerate.description = "Compiles the " + umpleSourceSet.umple + "."
        umpleGenerate.source = umpleSourceSet.umple
        project.tasks.getByName(sourceSet.classesTaskName).dependsOn taskName
    }
}