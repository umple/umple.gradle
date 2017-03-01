import org.junit.*
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.BuildLauncher
import java.util.Map
import java.util.HashMap
import cruise.umple.util.SampleFileWriter;

public class CompilationAndGeneration {    
    private static final DIRECTORIES_ROOT = "src/integrationTest/resources/TestProject/"
    private static final Map<String, ArrayList<String>> DIRECTORIES_TO_CHECK
    // Key = path to directory we want to check, value = the files we expect to find in the directory
    static {
        Map<String, ArrayList<String>> aMap = new HashMap<String, ArrayList<String>>()
        aMap.put(DIRECTORIES_ROOT + "subproj/build/classes/generatedSource", ["Subproj.class", "Subproj2.class"])
        aMap.put(DIRECTORIES_ROOT + "sub2/build/classes/generatedSource", ["Sub2.class", "Sub22.class"])
        aMap.put(DIRECTORIES_ROOT + "subproj/generated/java", ["Subproj.java", "Subproj2.java"])
        aMap.put(DIRECTORIES_ROOT + "sub2/generated/java", ["Sub2.java", "Sub22.java"])
        DIRECTORIES_TO_CHECK = Collections.unmodifiableMap(aMap)
    }

    private static final PATH_TO_TEST_PROJECT = "src/integrationTest/resources/TestProject"
    private static final TEST_PROJECT_TASK = "compileJava"

    @Before
    // Generates Java from master.ump and compiles the result
    public void setUp() {
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
    }
        
    @Test
    public void verifyGeneratedAndCompiledFiles() { 
		for (Map.Entry<String, ArrayList<String>> entry : DIRECTORIES_TO_CHECK.entrySet()) {
		    int fileCount = 0
		    File createdFolder = new File(entry.getKey())
		    for (File file: createdFolder.listFiles()) {   
		        String fileName = file.getName()            
		        if (file.length() == 0) {
		            fail(createdFolder.getPath() + ' folder contains an empty file')
		        }
		        
		        if (!verifyFileName(fileName, entry.getValue())) {
		            fail(createdFolder.getPath() + ' folder is missing ' + fileName)
		        }
		       
		        if (getFileExtension(fileName).equals("java")) {
		              String actual = SampleFileWriter.readContent(file);
                      SampleFileWriter.assertFileContent(new File(PATH_TO_TEST_PROJECT + "/verifiedGeneratedOutput", fileName), actual);
		        }
		        fileCount++
		    }
		    
		    if (fileCount != 2) {
		        fail(createdFolder.getName() + " folder contains an incorrect number of files")
		    }
		}    
    }

    @After
    public void tearDown() {
        for (String directory : DIRECTORIES_TO_CHECK) {
            clean(directory)
        }
    }
    
    private void clean(final String DIR_PATH) {
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
       
    private boolean verifyFileName(String fileName, ArrayList<String> allowableValues) {
        for (String s : allowableValues) {
            if (s.equals(fileName)) {
                return true
            }
        }
        
        return false
    }
    
    private String getFileExtension(String fileName) {
        String[] tokens = fileName.split("\\.")
        String fileExtension = tokens[tokens.length - 1]
        if (!fileExtension.equals("java") && !fileExtension.equals("class")) {
              fail(fileName + "is not of a recognized type")
        }
        
        return fileExtension
    }
}