import gitcity.GitLogstreamParser
import gitcity.model.ChangeLog
import gitcity.model.ChangeSet
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Month

class GitLogstreamParserTest {

    private var changeLog: ChangeLog = ChangeLog()
    private var changes: List<ChangeSet> = listOf()

    @Before
    fun prepare() {
        val source = Thread.currentThread().contextClassLoader.getResourceAsStream("logstream-1.txt")
        changeLog = GitLogstreamParser("logstream-1.txt", source).parse()
        changes = changeLog.changeSets
    }

    @Test
    fun testCommitCount() {
        assertEquals("wrong total commit count", 411, changes.size)
    }

    @Test
    fun testFirstCommitId() {
        assertEquals("wrong first commit", "3def5fb2878a0674266cc7edd7bb393874c5d0cf", changes.first().id)
    }

    @Test
    fun testFirstCommitDate() {
        val time = changes.first().time
        assertEquals("wrong first commit day", 19, time.dayOfMonth)
        assertEquals("wrong first commit month", Month.JANUARY, time.month)
        assertEquals("wrong first commit year", 2015, time.year)
    }

    @Test
    fun testLastCommitId() {
        assertEquals("wrong last commit", "2b5b82f3c337e58e0eff2ded7314698d50914cdf", changes.last().id)
    }

    @Test
    fun testFirstCommitFileChanges() {
        val firstCommit = changes.first()
        assertEquals("1 changed file in first commit", 1, firstCommit.fileChanges.size)
        assertEquals("3185 added lines in first file/commit", 3185, firstCommit.fileChanges.first().linesAdded)
        assertEquals("0 removed lines in first commit", 0, firstCommit.fileChanges.first().linesRemoved)
    }


}