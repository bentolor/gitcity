package gitcity.repository

import gitcity.GitCityOptions
import gitcity.info
import gitcity.trace
import java.io.BufferedReader
import java.io.InputStream
import java.io.Reader
import java.nio.file.Paths
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class GitLogstreamParser(private val opts: GitCityOptions, private val gitLogStream: InputStream) {

    fun parse(): ChangeLog {
        val changeSets = ArrayList<ChangeSet>()
        gitLogStream.buffered().reader().bufferedTracing().use { reader ->
            var changeHeader: String? = reader.readLine()
            while (changeHeader != null) {
                val sha1 = parseHash(changeHeader)
                val timeStamp = parseTimestamp(reader.readLine())
                val author = reader.readLine() ?: "null"
                val subject = reader.readLine() ?: "null"

                val fileChanges = ArrayList<FileChange>()
                var fileChangeStat: String? = reader.readLine()
                while (fileChangeStat != null && !fileChangeStat.isBlank() && !isHash(fileChangeStat)) {
                    val fileChange = parseFilechange(fileChangeStat)
                    if (fileChange != null) fileChanges.add(fileChange)
                    fileChangeStat = reader.readLine()
                }

                // Reverse order, so "first" commit is actual first in list
                changeSets.add(0, ChangeSet(sha1, timeStamp, author, subject, fileChanges))

                // prepare next change
                if (isHash(fileChangeStat))  // was this an empty commit?
                    changeHeader = fileChangeStat
                else
                    changeHeader = reader.readLine()
            }
        }

        info("Loaded and parsed ${changeSets.count()} commit statistics")
        return ChangeLog(opts.repoName, changeSets)
    }

    private fun parseHash(sha1: String): String {
        if (!sha1.matches("""^[\da-f]{40}$""".toRegex()))
            throw IllegalArgumentException("Invalid commit hash '$sha1'")
        return sha1
    }

    private fun isHash(possibleSha1: String?): Boolean  =  possibleSha1?.matches("""^[\da-f]{40}$""".toRegex()) ?: false

    private fun parseTimestamp(timestamp: String?): LocalDateTime {
        if (timestamp == null || !timestamp.matches("""^\d+$""".toRegex()))
            throw IllegalArgumentException("Invalid unix timestamp '$timestamp'")
        val instant = Instant.ofEpochSecond(timestamp.toLong())
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
    }

    private fun parseFilechange(fileChangeStat: String): FileChange? {
        val parseRegex = """^(\d+|-)\s+(\d+|-)\s+(.+)$""".toRegex()
        val matchResult = parseRegex.matchEntire(fileChangeStat) ?:
                throw IllegalArgumentException("Invalid file change stat line '$fileChangeStat'")

        val addedLine = matchResult.groupValues[2]
        val removedLine = matchResult.groupValues[1]
        val path = Paths.get(matchResult.groupValues[3])

        return if (addedLine == "-" || removedLine == "-") null
        else FileChange(path, addedLine.toInt(), removedLine.toInt())
    }

    /** Convert Reader into a tracing, buffered Reader */
    fun Reader.bufferedTracing(bufferSize: Int = DEFAULT_BUFFER_SIZE): BufferedTracingReader
            = if (this is BufferedTracingReader) this else BufferedTracingReader(this, bufferSize)

    /** Trace lines read. */
    class BufferedTracingReader(reader: Reader, size: Int) : BufferedReader(reader, size) {
        override fun readLine(): String? {
            val line: String? = super.readLine()
            if (line != null) trace(line)
            return line
        }
    }
}