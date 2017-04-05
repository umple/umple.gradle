import cruise.umple.UmpleLanguage
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.nio.file.Paths

import static junit.framework.Assert.assertEquals
import static org.junit.Assert.assertTrue
/**
 * Checks the behaviour of overriding globals in a project
 */
class CheckGlobals {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    
    @Before
    void setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }


    @Test
    void overrideDefaultGlobalValues() {

        buildFile << """
            // Add required plugins and source sets to the sub projects
            plugins { id "umple.gradle.plugin" } // Note must use this syntax

            // Override defaults
            umple {
              language = 'Php'
              master = file('tt-master.ump')
              outputDir = file('src/wat/\${language}')
            }
            
            task checkUmple {
                doLast {
                    Properties props = new Properties()
                    props.put('umple.language', umple.language.toString())
                    props.put('umple.master', umple.master.toString())
                    props.put('umple.outputDir', umple.outputDir.toString())
                    
                    println '${PropertiesUtil.PROP_START}'
                    props.store(System.out, null)
                    println ''
                    println '${PropertiesUtil.PROP_END}'
                }
            }
        """

        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('checkUmple')
                .build()

        Properties props = PropertiesUtil.getProperties(result.output)

        // TODO test not as a string..
        assertEquals("invalid generator", [UmpleLanguage.PHP].toString(), props.get("umple.language").toString())
        assertTrue("invalid file path", (props.get("umple.master").toString()).endsWith("tt-master.ump]"))
        assertEquals("invalid outputDir",
                Paths.get(testProjectDir.root.toString(), "src/wat/\${language}"),
                Paths.get((String)props.get("umple.outputDir")))
    }



}