package gitcity

import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.system.exitProcess

/**
 * Options describing a Git City run
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
data class GitCityOptions(
        var repoPath: Path = FileSystems.getDefault().getPath("."),
        var branchName: String = "master",
        var filter: Regex = Regex(".*")
) {
    val realPath: Path
        get() = repoPath.toRealPath()
    var repoName: String = realPath.fileName.toString()

    /** Parse CLI arguments into gitcity options. */
    fun parse(args: Array<String>): GitCityOptions {
        var pragma: String? = null
        for (arg in args) {
            if (arg.startsWith("--")) pragma = arg.substring(2)
            else if (pragma != null) {
                when(pragma) {
                    "help" -> { showHelp(); exitProcess(0) }
                    "repoPath" -> repoPath = FileSystems.getDefault().getPath(arg)
                    "filter" -> filter = Regex(arg)
                }
                pragma = null
            } else throw IllegalArgumentException("Malformed CLI params!")
        }
        return this
    }

    private fun showHelp() {
        println("Usage:")
        println(" --help                This help")
        println(" --repoPath <path>     Path to Git repository")
        println(" --filter <regex>      Only watch files matching the regex")
    }
}