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
    var port: Int = 8000

    /** Parse CLI arguments into gitcity options. */
    fun parse(args: Array<String>): GitCityOptions {
        var optionName: String? = null
        for (value in args) {
            if (value.startsWith("--")) {
                optionName = value.substring(2)
            } else if (value.startsWith("-")) {
                parseShortParam(value.substring(1))
            } else if (optionName != null) {
                parseParameterValue(optionName, value)
                optionName = null
            } else throw IllegalArgumentException("Malformed CLI option '$value' !")
        }
        return this
    }

    private fun parseShortParam(shortOption: String) {
        when(shortOption) {
            "vv" -> { TRACE = true; DEBUG = true}
            "v" -> { DEBUG = true}
            "h" -> showHelp()
            else -> throw IllegalArgumentException("Unknown CLI option '-$shortOption' !")
        }
    }

    private fun parseParameterValue(optionName: String, optionValue: String) {
        when (optionName) {
            "help" -> showHelp()
            "repoPath" -> repoPath = FileSystems.getDefault().getPath(optionValue)
            "filter" -> filter = Regex(optionValue)
            "port" -> port = Integer.parseInt(optionValue)
            else -> throw IllegalArgumentException("Unknown CLI option '--$optionName' !")
        }
    }

    private fun showHelp() {
        println("Usage:")
        println("  --help or -h          This help")
        println("  --repoPath <path>     Path to Git repository. Default: .")
        println("  --filter <regex>      Only watch files matching the regex. Default: .*")
        println("  --port <number>       Port number to start the websever. Default: 8080")
        println("  -v or -vv             Enable debug or trace log level")
        exitProcess(0)
    }
}