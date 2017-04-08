package cruise.umple.tasks

import cruise.umple.UmpleLanguage
/**
 * Defines the options required to configure compilation of umple files
 */
interface UmpleOptions {

    static final String NAME = "umple"

    public static final List<UmpleLanguage> DEFAULT_LANGUAGE_TO_GENERATE = [UmpleLanguage.JAVA]
    public static final List<File> DEFAULT_MASTER_FILE = [new File("Master.ump")]
    public static final File DEFAULT_GENERATED_OUTPUT = new File("generated/")
    public static final boolean DEFAULT_COMPILE_GENERATED_FLAG = true

    public static final LANGUAGE_TAG = "\\\$\\{language\\}"

    List<UmpleLanguage> getLanguage()
    void setLanguage(UmpleLanguage language)
    void setLanguage(List<UmpleLanguage> languages)

    List<File> getMaster()
    void setMaster(File master)
    void setMaster(List<File> masters)

    File getOutputDir()
    void setOutputDir(File f)

    Boolean getCompileGenerated()
    void setCompileGenerated(boolean f)

    /**
     * Gets the output directory relative to the current state
     * @param language
     * @return
     */
    File resolveOutputDir(UmpleLanguage language)
}