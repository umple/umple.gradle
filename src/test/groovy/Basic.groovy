import cruise.umple.UmpleGradlePlugin
import cruise.umple.tasks.UmpleOptions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static junit.framework.Assert.assertEquals
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

        assertEquals("languageToGenerate",
                UmpleOptions.DEFAULT_LANGUAGE_TO_GENERATE,
                config.languageToGenerate)
        assertEquals("umpleFilePath",
                UmpleOptions.DEFAULT_UMPLE_FILE,
                config.umpleFilePath)
    }
}