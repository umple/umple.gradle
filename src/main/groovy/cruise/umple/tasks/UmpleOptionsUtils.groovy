package cruise.umple.tasks

import cruise.umple.UmpleLanguage
import org.gradle.api.Project

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Created by kevin on 15/03/2017.
 */
class UmpleOptionsUtils {

    private UmpleOptionsUtils() { }


    static UmpleOptions forProject(Project project) {
        checkNotNull(project, "project == null")

        (UmpleOptions) project.extensions.getByName(UmpleOptions.NAME)
    }

    static File getOutputDir(File outputDir, UmpleLanguage language) {
        checkNotNull(outputDir, "outputDir == null")
        checkNotNull(language, "language == null")

        new File(outputDir.toString().replaceAll(UmpleOptions.LANGUAGE_TAG, language.toString().toLowerCase()))
    }
}
