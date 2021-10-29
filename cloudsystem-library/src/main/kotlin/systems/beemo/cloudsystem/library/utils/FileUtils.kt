package systems.beemo.cloudsystem.library.utils

import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.stream.Collectors

class FileUtils {

    companion object {
        fun getAllFiles(startDirectory: File): MutableList<File> {
            return Files.walk(startDirectory.toPath()).map { it.toFile() }.collect(Collectors.toList())
        }

        fun deleteFullDirectory(path: Path) {
            val files = path.toFile().listFiles() ?: return

            Arrays.stream(files).forEach {
                if (it.isDirectory) deleteFullDirectory(it.toPath())
                else it.delete()
            }

            path.toFile().delete()
        }

        fun deleteFullDirectory(path: File) {
            deleteFullDirectory(path.toPath())
        }

        fun deleteFullDirectory(path: String) {
            deleteFullDirectory(Paths.get(path))
        }

        fun copyFile(from: Path, to: Path) {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING)
        }

        fun copyFile(from: String, to: String) {
            copyFile(Paths.get(from), Paths.get(to))
        }

        fun copyFile(from: File, to: File) {
            copyFile(from.toPath(), to.toPath())
        }

        fun copyFileFromStream(inputStream: InputStream, to: String) {
            Files.copy(inputStream, Paths.get(to), StandardCopyOption.REPLACE_EXISTING)
        }

        fun copyCompiledFile(from: String, to: String) {
            val inputStream = this::class.java.classLoader.getResourceAsStream(from) ?: return
            Files.copy(inputStream, Paths.get(to), StandardCopyOption.REPLACE_EXISTING)
        }

        fun copyCompiledFile(from: Path, to: Path) {
            copyCompiledFile(from.toString(), to.toString())
        }

        fun copyCompiledFile(from: File, to: File) {
            copyCompiledFile(from.toString(), to.toString())
        }

        fun renameFile(file: File, name: String) {
            file.renameTo(File(name))
        }

        fun renameFile(file: Path, name: String) {
            renameFile(file.toFile(), name)
        }

        fun renameFile(file: String, name: String) {
            renameFile(Paths.get(file), name)
        }

        fun copyAllFiles(directory: Path, targetDirectory: String) {
            if (!Files.exists(directory)) return

            Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
                    return tryAndCopy(targetDirectory, directory, file)
                }
            })
        }

        fun copyAllFiles(directory: Path, targetDirectory: String, excluded: String) {
            if (!Files.exists(directory)) return

            Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
                    if (file.fileName.equals(excluded)) return FileVisitResult.CONTINUE

                    return tryAndCopy(targetDirectory, directory, file)
                }
            })
        }

        fun copyAllFiles(directory: Path, targetDirectory: String, excluded: Array<String>) {
            if (!Files.exists(directory)) return

            val excludedFiles: MutableList<Path> =
                Arrays.stream(excluded).map { Paths.get(it) }.collect(Collectors.toList())

            Files.walkFileTree(directory, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
                    if (excludedFiles.contains(file)) return FileVisitResult.CONTINUE

                    return tryAndCopy(targetDirectory, directory, file)
                }
            })
        }

        private fun tryAndCopy(targetDirectory: String, directory: Path, file: Path): FileVisitResult {
            val target = Paths.get(targetDirectory, directory.relativize(file).toString())
            val parent = target.parent

            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent)

            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)
            return FileVisitResult.CONTINUE
        }

        fun deleteIfExists(path: Path) {
            Files.deleteIfExists(path)
        }

        fun deleteIfExists(path: File) {
            deleteIfExists(path.toPath())
        }

        fun deleteIfExists(path: String) {
            deleteIfExists(Paths.get(path))
        }

        fun deleteOnExit(file: File) {
            file.deleteOnExit()
        }

        fun deleteOnExit(path: Path) {
            deleteOnExit(path.toFile())
        }

        fun createDirectory(path: Path) {
            path.toFile().mkdirs()
        }

        fun readStringFromFile(file: File): String {
            val bufferedReader = BufferedReader(FileReader(file))
            return bufferedReader.readLine()
        }

        fun writeStringToFile(file: File, value: String) {
            val bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file), StandardCharsets.UTF_8))

            bufferedWriter.write(value)
            bufferedWriter.flush()
            bufferedWriter.close()
        }
    }
}