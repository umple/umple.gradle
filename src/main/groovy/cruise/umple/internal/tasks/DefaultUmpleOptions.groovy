package cruise.umple.internal.tasks

import cruise.umple.UmpleLanguage
import cruise.umple.tasks.UmpleOptions

/**
 * Created by kevin on 15/03/2017.
 */
class DefaultUmpleOptions implements UmpleOptions {

    def UmpleLanguage languageToGenerate
    def File umpleFilePath
    def File generatedOutputPath

    DefaultUmpleOptions() {
        languageToGenerate = DEFAULT_LANGUAGE_TO_GENERATE
        umpleFilePath = DEFAULT_UMPLE_FILE
        generatedOutputPath = DEFAULT_GENERATED_OUTPUT_PATH
    }
    
    @Override
    void setLanguageToGenerate(UmpleLanguage language) {
        this.languageToGenerate = language
    }

    @Override
    void setUmpleFilePath(File path) {
        this.umpleFilePath = path
    }

    @Override
    void setUmpleFilePath(String path) {
        this.umpleFilePath = new File(path)
    }
    
    @Override
    void setGeneratedOutputPath(File path) {
        this.generatedOutputPath = path
    }

    @Override
    void setGeneratedOutputPath(String path) {
        this.generatedOutputPath = new File(path)
    }
}
