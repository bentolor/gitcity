package gitcity.mapping.building

import gitcity.ChangeLogAnalysis
import gitcity.mapping.treemap.Rect
import gitcity.mapping.treemap.SquarifiedLayout
import gitcity.mapping.treemap.TreeModel
import gitcity.repository.RepoFile
import java.lang.Math.pow


/**
 * Various algorithms, settings and heuristics to influence the placement, sizing and other visual properties of the "buildings" in
 * GitCity representing the files.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class BuildingMapper(val analysis: ChangeLogAnalysis, val worldLength: Double = 30.0) {

    val treeMapByEpoch: MutableMap<String, TreeModel>
    val epochIds: List<String>

    val sourceWorldVolume: Double
    val sourceWorldArea: Double

    init {
        epochIds = analysis.epochs.map { it.changeSet.id }

        val tree = analysis.epochs.last().fileTree

        sourceWorldVolume = tree.lineCount.toDouble()
        sourceWorldArea = sourceWorldVolume / pow(sourceWorldVolume, 1.0 / 3)

        val worldRect = Rect(-worldLength / 2, -worldLength / 2, worldLength, worldLength)

        // We build the "final built" city based on the very last commit
        val finishedCity = buildFinalCity(tree, worldRect)
        treeMapByEpoch = mutableMapOf(Pair(epochIds.last(), finishedCity))

        // now iterate over remaining epochs & build a copy of the final city with adjusted properties (height)
        for (epochId in epochIds) {
            treeMapByEpoch.getOrPut(epochId, { buildInterimsCity(finishedCity, epochId) })
        }
    }

    private fun buildFinalCity(tree: RepoFile, worldRect: Rect): TreeModel {
        val lastEpochTreeMap = tree.toTreeModel(this)
        lastEpochTreeMap.mappable.bounds = worldRect
        lastEpochTreeMap.layout(SquarifiedLayout())
        lastEpochTreeMap.accept(PostProcessing())
        return lastEpochTreeMap
    }

    private fun buildInterimsCity(finishedCity: TreeModel, epochId: String): TreeModel {
        val epoch = analysis.epoch(epochId)
        return epoch.fileTree.buildTreeModelByTemplate(finishedCity, this)
    }

    /** Calculate desired building area based on line count (building volume). */
    fun dimBuilding(file: RepoFile): BuildingProperties {
        val sourceVolume = file.lineCount.toDouble()

        // Height assuming a cubic with 12 equals sides
        val sourceHeight = pow(sourceVolume, 1.0 / 3)

        // resulting ground area would be
        val sourceArea = sourceVolume / sourceHeight

        val relativeVolume = sourceVolume / sourceWorldVolume
        val relativeArea = sourceArea / this.sourceWorldArea

        return BuildingProperties(sourceArea, sourceHeight, relativeArea, relativeVolume)
    }

}

