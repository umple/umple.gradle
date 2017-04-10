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
    void simpleGenerationGlobalDefaults() {

        buildFile << """
            // Add required plugins and source sets to the sub projects
            plugins { id "umple.gradle.plugin" } // Note must use this syntax

            // Override defaults
            sourceSets {
                main { umple { } }
            }
            
            umple {
              //TODO find out why this gives "No enum constant cruise.umple.UmpleLanguage.Java"
              //language = Arrays.asList('Java', 'Php', 'Ruby', 'Sql', 'Cxx')
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
       
       /* assertTrue(testProjectDir.root.toPath().resolve("out/php/").toFile().isDirectory())
        assertTrue(testProjectDir.root.toPath().resolve("out/ruby/").toFile().isDirectory())
        assertTrue(testProjectDir.root.toPath().resolve("out/sql/").toFile().isDirectory())
        assertTrue(testProjectDir.root.toPath().resolve("out/cxx/").toFile().isDirectory())*/
        //TODO add verifiction for the files in the directory?
    }

}
