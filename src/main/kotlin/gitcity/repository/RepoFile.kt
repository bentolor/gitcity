package gitcity.repository

import java.nio.file.Path

/** Statistics about a file within a RepoEpoch. */
data class RepoFile(
        var name: String,
        var parent: RepoFile? = null,
        var children: MutableMap<String, RepoFile>? = null,
        var lineCount: Int = 0,
        var fileCount: Int = 0,
        var dirCount: Int = 0
) {

    val isLeaf: Boolean
        get() = children == null

    val path: String
        get() {
            val p = parent
            // topmost parent is the repository directory. Skip that
            return if (p?.parent != null) (p?.path ?: "") + '/' + name else name
        }

    fun getPathFiles(filePath: String): List<RepoFile> = this.getPathFiles(java.io.File(filePath).toPath())

    /**
     * Convert a file path into a list of Model files.
     * Each pathelement is reflected by one File.
     */
    fun getPathFiles(filePath: Path): List<RepoFile> {
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

    override fun toString(): String = toString(false)
    fun toString(recurse: Boolean): String {
        if (!recurse) return "$name [$lineCount]"
        var indent = ""
        var currentParent = parent
        while (currentParent != null) {
            indent += "  "
            currentParent = currentParent.parent
        }
        var myString = "\n$indent$name [$lineCount]"
        myString = children?.values?.fold(myString, { s, f -> s + f.toString() }) ?: myString
        return myString
    }
}

