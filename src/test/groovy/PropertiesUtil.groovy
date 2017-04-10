import static org.junit.Assert.assertTrue

/**
 * Utility for testing build scripts
 */
class PropertiesUtil {

    static final PROP_START = 'PROPERTIES_START'
    static final PROP_END = 'PROPERTIES_END'
    
    static final LANGUAGE_KEY = 'umple.language'
    static final MASTER_KEY = 'umple.master'
    static final OUT_DIR_KEY = 'umple.outputDir'
    static final COMPILE_GENERATED_KEY = 'umple.compileGenerated'
    static final CUSTOM_MASTER_PATH_KEY = 'umple.customMasterPath'

    static Properties getProperties(String input) {
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
