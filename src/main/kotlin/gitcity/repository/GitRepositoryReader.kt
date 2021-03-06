package gitcity.repository

import gitcity.GitCityOptions
import gitcity.info

/**
 * Extract a git repository changelog into the gitcity gitcity.model.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class GitRepositoryReader {

    var GIT_BINARY = "git"

    /**
     * Execute Git to log the changes in a Git repository
     */
    fun parseRepository(opts: GitCityOptions): ChangeLog {
        info("Dumping history of branch ${opts.branchName} of Git repository located at ${opts.realPath}")

        // Ouput separated by newlines:
        //   SHA1, Unix Timestamp, Authorname, Subject, "<deleted lines> <added lines> <path>", <empty line>
        val cmdLine = "$GIT_BINARY  log ${opts.branchName} " +
                "-w -b -R --no-merges --date-order --no-renames " +
                "--numstat --format=format:%H%n%at%n%aN%n%s"

        info("Executing: $cmdLine")
        val gitProcess = Runtime.getRuntime().exec(cmdLine, arrayOf(), opts.realPath.toFile())
        val gitLogStream = gitProcess.inputStream

        return GitLogstreamParser(opts, gitLogStream).parse()
    }

    /**
     * Execute Git to log the changes in a Git repository
     */
    fun parseGitLogFile(opts: GitCityOptions): ChangeLog {
        val logFile = opts.gitLog ?: throw IllegalStateException("logfile is null?")
        info("Using git log output in file $logFile")
        return GitLogstreamParser(opts, logFile.toFile().inputStream()).parse()
    }

}


