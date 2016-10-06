package gitcity

import gitcity.repository.ChangeLog
import gitcity.repository.ChangeSet
import gitcity.repository.RepoEpoch
import gitcity.repository.RepoFile

/**
 * Analyze a git changelog and calculates statistics on a timeline
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class ChangeLogAnalysis(val opts: GitCityOptions, changeLog: ChangeLog) {

    val epochs: List<RepoEpoch>

    init {
        info("Beginning revision/LOC analysis ...")
        val epochs = mutableListOf<RepoEpoch>()
        var lastEpochTree: RepoFile = RepoFile(changeLog.name)
        for (changeSet in changeLog.changeSets) {
            val newEpoch = calculateNewEpoch(lastEpochTree, changeSet)
            lastEpochTree = newEpoch.fileTree
            epochs.add(newEpoch)
        }
        this.epochs = epochs
        info("Last commit contains a total of ${lastEpochTree.lineCount} lines in ${lastEpochTree.fileCount} files " +
                "and ${lastEpochTree.dirCount} directories")
    }

    fun epoch(epochId: String): RepoEpoch = epochs.find { it.changeSet.id == epochId } ?: throw IllegalStateException("Unknown: $epochId")

    private fun calculateNewEpoch(lastEpochTree: RepoFile, changeSet: ChangeSet): RepoEpoch {
        // make a full clone
        val newTree = copySubTree(null, lastEpochTree)
        // iterate over all changed files
        changeSet.fileChanges.forEach { fileChange ->
            // effective linechange
            val lineDelta = fileChange.linesAdded - fileChange.linesRemoved
            // add delta to all path elements
            val pathFiles = newTree.getPathFiles(fileChange.filePath)
            pathFiles.forEach { pathElement ->
                pathElement.lineCount += lineDelta
            }
            // ... and to the holding file element
            newTree.lineCount += lineDelta
        }
        return RepoEpoch(changeSet, newTree)
    }

    /**
     * Make a full, independent clone of a previous file tree. Intended for new epochs which will change it.
     */
    private fun copySubTree(parent: RepoFile?, subTree: RepoFile): RepoFile {
        val origChilds = subTree.children
        val clone = subTree.copy(children = null, parent = parent)
        if (origChilds != null) {
            val newChildren = mutableMapOf<String, RepoFile>()
            origChilds.forEach { e -> newChildren.put(e.key, copySubTree(clone, e.value)) }
            clone.children = newChildren
        }
        return clone
    }

}