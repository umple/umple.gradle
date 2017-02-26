import org.junit.*
import static org.junit.Assert.*
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import org.gradle.tooling.* //GradleConnector, ProjectConnection, BuildLauncher

public class UmpleIntegrationTests {
    private static Project testProject
    private static final DIRECTORIES_TO_CHECK = 
    [
        "src${File.separator}integrationTest${File.separator}resources${File.separator}TestProject${File.separator}subproj${File.separator}build${File.separator}classes${File.separator}generatedSource",
        "src${File.separator}integrationTest${File.separator}resources${File.separator}TestProject${File.separator}subproj${File.separator}generated${File.separator}java",
        "src${File.separator}integrationTest${File.separator}resources${File.separator}TestProject${File.separator}sub2${File.separator}build${File.separator}classes${File.separator}generatedSource",
        "src${File.separator}integrationTest${File.separator}resources${File.separator}TestProject${File.separator}sub2${File.separator}generated${File.separator}java"
    ] 
    private static final PATH_TO_TEST_PROJECT = "src${File.separator}integrationTest${File.separator}resources${File.separator}TestProject"
    private static final TEST_PROJECT_TASK = "compileJava"

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
    
    // Try generating Java from master.ump and compiling the result
    @Test
    public void generateAndCompileJavaFiles() {        
        GradleConnector connector = GradleConnector.newConnector()
        connector.forProjectDirectory(new File(PATH_TO_TEST_PROJECT))
        ProjectConnection connection = connector.connect()
        try {
            BuildLauncher launcher = connection.newBuild()
            launcher.forTasks(TEST_PROJECT_TASK)
            launcher.run()
        } finally {
            connection.close()
        }
        
        for (String current : DIRECTORIES_TO_CHECK) {
            int count = 0
            File createdFolder = new File(current)
            for (File file: createdFolder.listFiles()) {
                if (file.length() == 0) {
                    fail(createdFolder.getName() + ' folder contains an empty file')
                }
                count++
            }

            if (count != 2) {
                fail(createdFolder.getName() + ' folder contains an incorrect number of files')
            }

            count = 0
        }
    }

    // TODO test overriding default plugin configurations once a configuration strategy has been decided upon. See 

    @After
    public void tearDown() {
        for (String directory : DIRECTORIES_TO_CHECK) {
            clean(directory)
        }
    }
    
    public void clean(final String DIR_PATH) {
        try {
            File dir = new File(DIR_PATH);
            for (File file:dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        } catch (Exception e) {
            fail(e.getMessage)
        }
       
    }
}