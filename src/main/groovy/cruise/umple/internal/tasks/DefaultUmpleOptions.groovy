package cruise.umple.internal.tasks

import com.google.common.base.MoreObjects
import cruise.umple.UmpleLanguage
import cruise.umple.tasks.UmpleOptions

import static com.google.common.base.Preconditions.checkNotNull
/**
 * Default implmentation of the UmpleOptions interface
 */
class DefaultUmpleOptions implements UmpleOptions {

    def List<UmpleLanguage> language
    def List<File> master
    def File outputDir // we can't use the name output. It's reserved by the java plugin

    DefaultUmpleOptions() {
        language = null
        master = null
        outputDir = null
    }

    @Override
    void setLanguage(UmpleLanguage language) {
        this.language = [checkNotNull(language, "language == null")]
    }

    @Override
    void setMaster(File master) {
        println("Setting user value for master in DefaultUmpleOptions: " + master)
        this.master = [checkNotNull(master, "master == null")]
    }

    @Override
    void setLanguage(List<UmpleLanguage> languages) {
        this.language = checkNotNull(languages, "languages == null")
    }

    @Override
    void setMaster(List<File> masters) {
        this.master = checkNotNull(masters, "masters == null")
    }

    @Override
    void setOutputDir(File output) {
        this.outputDir = checkNotNull(output, "output == null")
    }

    @Override
    String toString() {
        MoreObjects.toStringHelper(this)
                .add("language", language)
                .add("master", master)
                .add("output", outputDir)
                .toString()
    }
}
