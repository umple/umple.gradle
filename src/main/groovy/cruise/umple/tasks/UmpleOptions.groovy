package cruise.umple.tasks

import cruise.umple.UmpleLanguage
/**
 * Created by kevin on 15/03/2017.
 */
interface UmpleOptions {

    static final String NAME = "umple"

    public static final UmpleLanguage DEFAULT_LANGUAGE_TO_GENERATE = UmpleLanguage.JAVA
    public static final File DEFAULT_UMPLE_FILE = new File("src/umple/Master.ump")
    public static final File DEFAULT_GENERATED_OUTPUT_PATH = new File("generated/java")

    UmpleLanguage getLanguageToGenerate()

    void setLanguageToGenerate(UmpleLanguage language)

    File getUmpleFilePath()

    void setUmpleFilePath(File path)
    void setUmpleFilePath(String path)
    
    File getGeneratedOutputPath()
    
    void setGeneratedOutputPath(File path)
    void setGeneratedOutputPath(String path)
    


}