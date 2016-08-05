package gitcity

import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * Options describing a Git City run
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
data class GitCityOptions(
        var repoPath: Path = FileSystems.getDefault().getPath("."),
        var branchName: String = "master"
) {
    val realPath: Path
        get() = repoPath.toRealPath()
    var repoName: String = realPath.fileName.toString()

    /** Parse CLI arguments into gitcity options. */
    fun parse(args: Array<String>): GitCityOptions {
        assert(args.size > -1)
        return this
    }
}