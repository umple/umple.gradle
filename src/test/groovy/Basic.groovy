import cruise.umple.UmpleGradlePlugin
import cruise.umple.tasks.UmpleOptions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNull
import static org.junit.Assert.assertTrue

class Basic {
    private Project testProject

    @Before
    void setUp() {
        testProject = ProjectBuilder.builder().build()
        testProject.plugins.apply UmpleGradlePlugin
    }

    @Test
    void applyUmplePluginToProject() {
        assertTrue('Umple plugin was not applied', testProject.plugins.hasPlugin(UmpleGradlePlugin))
    }

    @Test
    void hasDefaultGlobalParams() {

        UmpleOptions config = testProject.umple

        assertEquals("language", [], config.language)
        assertEquals("master", [], config.master)
        assertNull("output", config.outputDir)
    }

    // TODO Tests that use the mutators and validate it
}