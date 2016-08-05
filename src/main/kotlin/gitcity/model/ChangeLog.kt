package gitcity.model

import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*

/** Data gitcity.model representing a stream of file changes in a repository. */
data class ChangeLog(val name: String = "A repository",
                     val changeSets: List<ChangeSet> = ArrayList<ChangeSet>())

data class ChangeSet(val id: String,
                     val time: LocalDateTime,
                     val author: String,
                     val subject: String,
                     val fileChanges: List<FileChange>)

data class FileChange(val filePath: Path,
                      val linesAdded: Int,
                      val linesRemoved: Int) {

    constructor(filePath: String,
                linesAdded: Int,
                linesRemoved: Int) : this(toPath(filePath), linesAdded, linesRemoved)
}

internal fun toPath(pathString: String): Path = File(pathString).toPath()

