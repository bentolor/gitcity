package gitcity.model

import java.io.File
import java.nio.file.Path

/**
 * Convert a file path into a list of Model files.
 * Each pathelement is reflected by one File.
 */
fun RepoFile.getPathFiles(filePath: Path): List<RepoFile> {
    val fileName = filePath.getName(0).toString()
    val isLeaf = filePath.nameCount < 2
    val children = this.children ?: mutableMapOf()
    this.children = children

    val wantedFile = children[fileName] ?: {
        val newFile = RepoFile(fileName, this)
        val oldFile = children.put(fileName, newFile)
        if (oldFile == null) {
            var curr: RepoFile? = this
            while (curr != null) {
                if (isLeaf) curr.fileCount++ else curr.dirCount++
                curr = curr.parent
            }
        }
        newFile
    }()

    return if (isLeaf) listOf(wantedFile)
    else listOf(wantedFile) + wantedFile.getPathFiles(filePath.subpath(1, filePath.nameCount))
}

fun RepoFile.getPathFiles(filePath: String): List<RepoFile> = this.getPathFiles(File(filePath).toPath())