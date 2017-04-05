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
                    
                    println '${PROP_START}'
                    props.store(System.out, null)
                    println ''
                    println '${PROP_END}'
                }
            }
        """

        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('checkUmple')
                .build()

        Properties props = getProperties(result.output)

        // TODO test not as a string..
        assertEquals("invalid generator", [UmpleLanguage.PHP].toString(), props.get("umple.language").toString())
        assertTrue("invalid file path", (props.get("umple.master").toString()).endsWith("tt-master.ump]"))
        assertEquals("invalid outputDir",
                Paths.get(testProjectDir.root.toString(), "src/wat/\${language}"),
                Paths.get((String)props.get("umple.outputDir")))
    }

    private static final PROP_START = 'PROPERTIES_START'
    private static final PROP_END = 'PROPERTIES_END'

    private static Properties getProperties(String input) {
        assertTrue('Could not find start of properties in output', input.contains(PROP_START))
        assertTrue('Could not find end of properties in output', input.contains(PROP_END))
        int start = input.indexOf(PROP_START) + PROP_START.size()
        int end = input.indexOf(PROP_END)

        Properties res = new Properties()
        StringReader reader = new StringReader(input.substring(start, end).trim())
        try {
            res.load(reader)
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe)
        } finally {
            reader.close()
        }

        res
    }

}