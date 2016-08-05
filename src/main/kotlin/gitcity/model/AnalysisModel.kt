package gitcity.model

/**Data models to store the timeline gitcity.model */
data class RepoEpoch(val changeSet: ChangeSet,
                     val fileTree: RepoFile)

data class RepoFile(
        var name: String,
        var parent: RepoFile? = null,
        var children: MutableMap<String, RepoFile>? = null,
        var lineCount: Int = 0,
        var fileCount: Int = 0,
        var dirCount: Int = 0
) {
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
