import gitcity.ChangeLogAnalysis
import gitcity.GitCityOptions
import gitcity.model.*
import gitcity.repository.ChangeLog
import gitcity.repository.ChangeSet
import gitcity.repository.FileChange
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class ChangeLogAnalysisTest {

    @Test
    fun testSimpleChangelog() {
        val changes1 = listOf(
                FileChange("foo.js", 3, 0),
                FileChange("baz.js", 5, 0),
                FileChange("loo/a.js", 11, 0),
                FileChange("loo/b.js", 20, 0),
                FileChange("loo/ho/.placeholder", 0, 0)
        )
        val set1 = ChangeSet("1", LocalDateTime.now(), "Bernie", "Hack 1", changes1)

        val changes2 = listOf(
                FileChange("baz.js", 1, 3),
                FileChange("loo/b.js", 0, 10)
        )
        val set2 = ChangeSet("2", LocalDateTime.now(), "Bernie", "Hack 2", changes2)

        val log = ChangeLog(changeSets = listOf(set1, set2))

        val res = ChangeLogAnalysis(GitCityOptions(), log)
        assertEquals(2, res.epochs.size)

        val lastEpoch = res.epochs.last()

        val looBjs = lastEpoch.fileTree.getPathFiles("loo/b.js")
        assertEquals(10, looBjs[1].lineCount)
        assertEquals(21, looBjs[0].lineCount)

        assertEquals(27, lastEpoch.fileTree.lineCount)
        assertEquals(5, lastEpoch.fileTree.fileCount)
        assertEquals(2, lastEpoch.fileTree.dirCount)
    }

}