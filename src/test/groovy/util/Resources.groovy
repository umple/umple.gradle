package util

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * Created by kevin on 05/04/2017.
 */
class Resources {

    static void copyDirectory(URL resourceUrl, File directory) {
        Path target = directory.toPath()
        File sourceFile = new File(resourceUrl.toURI())
        Path source = sourceFile.toPath()

        Files.walkFileTree(sourceFile.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
            new SimpleFileVisitor<Path>() {

                @Override
                FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = target.resolve(source.relativize(dir))

                    try {
                        Files.copy(dir, targetDir)
                    } catch (FileAlreadyExistsException e) {
                        if (!Files.isDirectory(targetDir))
                            throw e
                    }
                    return FileVisitResult.CONTINUE
                }

                @Override
                FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(source.relativize(file)))
                    return FileVisitResult.CONTINUE
                }
            })
    }
}
