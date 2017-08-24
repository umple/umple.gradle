package cruise.umple.internal.tasks

import com.google.common.base.MoreObjects
import cruise.umple.UmpleLanguage
import cruise.umple.tasks.UmpleOptions
import cruise.umple.tasks.UmpleOptionsUtils

import static com.google.common.base.Preconditions.checkNotNull
/**
 * Default implmentation of the UmpleOptions interface
 */
class DefaultUmpleOptions implements UmpleOptions {

    private List<UmpleLanguage> language
    private List<File> master
    private File outputDir // we can't use the name output. It's reserved by the java plugin
    private Boolean compileGenerated // tells us whether or not to set the compileJava task to depend on the compileUmple task
    private Boolean customMasterPath // tells us whether user wants to override src/{source set}/umple structure
   
    DefaultUmpleOptions() {
        language = []
        master = []
        outputDir = null
        // null means no configuration has been provided by the user. We use the check `compileGenerated == null` in UmpleGradlePlugin
        // to determine if we need to check for global defaults or use our hardcoded defaults. We must use a Boolean here (as opposed to a boolean)
        // so we can set the initial value to null
        compileGenerated = null 
        customMasterPath = null
    }

    @Override
    void setLanguage(UmpleLanguage language) {
        this.language = [language] // null check etc in setLanguage(List<UmpleLanguage> languages)
    }

    @Override
    void setLanguage(List<UmpleLanguage> languages) {
        this.language.clear()
        for (UmpleLanguage language : languages) 
        {
            this.language.add(checkNotNull(language, "language == null"))
        }
    }

    @Override
    List<UmpleLanguage> getLanguage() {
        this.language
    }

    @Override
    void setMaster(File master) {
        this.master = [master] // null check etc in setMaster(List<File> masters)
    }
    
    @Override
    void setMaster(List<File> masters) {
        this.master.clear()
        for (File master : masters) 
        {
            this.master.add(checkNotNull(master, "master == null"))
        }
    }

    @Override
    List<File> getMaster() {
        this.master
    }

    @Override
    void setOutputDir(File output) {
        this.outputDir = checkNotNull(output, "output == null")
    }

    @Override
    File getOutputDir() {
        this.outputDir
    }
    
    @Override
    void setCompileGenerated(boolean val) {
        this.compileGenerated = val
    }

    @Override
    Boolean getCompileGenerated() {
        this.compileGenerated
    }
    
    @Override
    void setCustomMasterPath(boolean val) {
        this.customMasterPath = val
    }

    @Override
    Boolean getCustomMasterPath() {
        this.customMasterPath
    }
    
    @Override
    File resolveOutputDir(UmpleLanguage language) {
        UmpleOptionsUtils.getOutputDir(outputDir, language)
    }

    @Override
    String toString() {
        MoreObjects.toStringHelper(this)
                .add("language", language)
                .add("master", master)
                .add("output", outputDir)
                .add("compileGenerated", compileGenerated)
                .add("customMasterPath", customMasterPath)
                .toString()
    }
}
