package cruise.umple.internal.tasks

import cruise.umple.UmpleLanguage
import cruise.umple.tasks.UmpleOptions

/**
 * Created by kevin on 15/03/2017.
 */
class DefaultUmpleOptions implements UmpleOptions {

    def UmpleLanguage languageToGenerate
    def File umpleFilePath

    DefaultUmpleOptions() {
        languageToGenerate = DEFAULT_LANGUAGE_TO_GENERATE
        umpleFilePath = DEFAULT_UMPLE_FILE
    }

    @Override
    void setUmpleFilePath(File path) {
        this.umpleFilePath = path
    }

    @Override
    void setUmpleFilePath(String path) {
        this.umpleFilePath = new File(path)
    }

}
