package cruise.umple.tasks

import org.gradle.api.Project

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Created by kevin on 15/03/2017.
 */
class UmpleOptionsUtils {
    private UmpleOptionsUtils() {

    }


    static UmpleOptions forProject(Project project) {
        checkNotNull(project, "project == null")

        (UmpleOptions) project.extensions.getByName("umple")
    }
}
