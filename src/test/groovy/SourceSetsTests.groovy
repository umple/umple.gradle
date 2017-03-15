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
class SourceSetsTests {

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
            
            sourceSets {
                main {
                    umple { }
                }
            }

            umple {
              languageToGenerate = 'Java'
              umpleFilePath = file('master.ump')
            }
            
            task checkUmple {
                doLast {
                    Properties props = new Properties()
                    props.put('umple.languageToGenerate', umple.languageToGenerate.toString())
                    props.put('umple.umpleFilePath', umple.umpleFilePath.toString())
                    
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

        assertEquals("invalid generator", UmpleLanguage.valueOf(props.get("umple.languageToGenerate").toString()), UmpleLanguage.PHP)
        assertTrue("invalid file path", Paths.get((String)props.get("umple.umpleFilePath")).endsWith("master.ump"))
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