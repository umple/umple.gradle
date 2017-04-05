import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import util.Resources

import static org.junit.Assert.assertTrue

/**
 * Created by kevin on 05/04/2017.
 */
class GenerationTests {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    @Before
    void setup() {
        Resources.copyDirectory(GenerationTests.getResource("/generation-tests/simple-project"), testProjectDir.root)
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Test
    void simpleGenerationDefaults() {

        buildFile << """
            // Add required plugins and source sets to the sub projects
            plugins { id "umple.gradle.plugin" } // Note must use this syntax

            // Override defaults
            sourceSets {
                main { umple { } }
            }
            
            umple {
              language = 'Java'
              master = file('master.ump')
              outputDir = file('out/\\\${language}')
            }
        """

        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('build')
                .build()

        println result.output.toString()
        assertTrue(testProjectDir.root.toPath().resolve("out/java/").toFile().isDirectory())
    }

}
