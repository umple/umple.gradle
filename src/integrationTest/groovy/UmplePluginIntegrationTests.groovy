import org.junit.*
import static org.junit.Assert.assertTrue // for assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

public class UmpleIntegrationTests {
    private static Project testProject

    @Before
    public void setUp() {
        testProject = ProjectBuilder.builder().build()
        testProject.plugins.apply 'umple.gradle.plugin'
    }

    @Test
    public void umpleApplyPluginToProject() {      
        assertTrue(testProject.getPlugins().hasPlugin('umple.gradle.plugin'))
    }

    @Test
    public void umplePluginAddsTaskToProject() {
        assertTrue(testProject.tasks.getByName('generateSource') != null)
    }
}