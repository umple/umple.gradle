import cruise.umple.UmpleLanguage
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static PropertiesUtil.*

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
              compileGenerated = false
              customMasterPath = true
              
            }
            
            task checkUmple {
                doLast {
                    Properties props = new Properties()

                    props.setProperty('${LANGUAGE_KEY}', umple.language.get(0).toString())
                    props.setProperty('${MASTER_KEY}', umple.master.get(0).toString())
                    props.setProperty('${OUT_DIR_KEY}', umple.outputDir.toString())
                    props.setProperty ('${COMPILE_GENERATED_KEY}', umple.compileGenerated.toString())
                    props.setProperty ('${CUSTOM_MASTER_PATH_KEY}', umple.customMasterPath.toString())
                    
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

        Properties props = PropertiesUtil.getProperties(result.output)

        // TODO test not as a string..
        assertEquals("invalid generator", UmpleLanguage.PHP.toString(), props.getProperty("${LANGUAGE_KEY}"))
        assertTrue("invalid file path", (props.getProperty("${MASTER_KEY}")).endsWith("tt-master.ump"))
        assertEquals("invalid outputDir",
                Paths.get(testProjectDir.root.toString(), "src/wat/\${language}"),
                Paths.get((props.getProperty("${OUT_DIR_KEY}"))))
        assertEquals("invalid compileGenerated", "false", props.getProperty("${COMPILE_GENERATED_KEY}"))
        assertEquals("invalid customMasterPath", "true", props.getProperty("${CUSTOM_MASTER_PATH_KEY}"))
    }



}