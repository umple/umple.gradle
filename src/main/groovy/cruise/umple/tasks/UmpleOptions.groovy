package cruise.umple.tasks

import cruise.umple.UmpleLanguage
/**
 * Created by kevin on 15/03/2017.
 */
interface UmpleOptions {

    static final String NAME = "umple"

    public static final UmpleLanguage DEFAULT_LANGUAGE_TO_GENERATE = UmpleLanguage.JAVA
    public static final File DEFAULT_UMPLE_FILE = new File("src/umple/Master.ump")

    UmpleLanguage getLanguageToGenerate()

    void setLanguageToGenerate(UmpleLanguage language)

    File getUmpleFilePath()

    void setUmpleFilePath(File path)
    void setUmpleFilePath(String path)


}