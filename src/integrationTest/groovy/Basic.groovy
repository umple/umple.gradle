import org.junit.*
import static org.junit.Assert.assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

public class Basic {
    private static Project testProject
    
    @Before
    public void setUp() {
        testProject = ProjectBuilder.builder().build()
        testProject.plugins.apply 'umple.gradle.plugin'
    }

    @Test
    public void applyUmplePluginToProject() {      
        assertTrue('Umple plugin was not applied', testProject.getPlugins().hasPlugin('umple.gradle.plugin'))
    }

    @Test
    public void umplePluginAddsTaskToProject() {
        assertTrue('generateSource task was not added to project', testProject.tasks.getByName('generateSource') != null)
    }
}