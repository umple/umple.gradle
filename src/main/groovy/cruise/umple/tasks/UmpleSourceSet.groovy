package cruise.umple.tasks

import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
/**
 * Denotes a SourceSet of Umple sources.
 *
 * Based on {@link org.gradle.api.tasks.ScalaSourceSet}.
 */
interface UmpleSourceSet {

    /**
     * Returns the source that will be compiled by Umple
     * @return Umple source, never {@code null}
     */
    SourceDirectorySet getUmple()

    /**
     * Returns the source that will be compiled by Umple and configures using the closure
     * @param configureClosure closure for configuration
     *
     * @return Umple source, never {@code null}
     */
    UmpleSourceSet umple(Closure configureClosure)

    /**
     * Returns the source that will be compiled by Umple and configures using the closure
     * @param configureAction action for configuration
     *
     * @return Umple source, never {@code null}
     */
    UmpleSourceSet umple(Action<UmpleOptions> configureAction)
    //UmpleSourceSet umple(Action<? super SourceDirectorySet> configureAction)
}