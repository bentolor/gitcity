package gitcity.mapping.building

import gitcity.ChangeLogAnalysis
import gitcity.mapping.treemap.TreeModel
import gitcity.repository.RepoFile
import gitcity.mapping.treemap.MapItem
import gitcity.mapping.treemap.Mappable
import gitcity.mapping.treemap.Rect
import gitcity.mapping.treemap.SquarifiedLayout

/**
 * Various algorithms, settings and heuristics to influence the placement, sizing and other visual properties of the "buildings" in
 * GitCity representing the files.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
//val STREET_WIDTH = 8

class BuildingMapper(analysis: ChangeLogAnalysis) {

    private val BUILDING_MAX_HEIGHT = 120
    private val BUILDING_MAX_HEIGHT_VARIANCE = 20

    val treeMap: TreeModel
    val tree = analysis.epochs.last().fileTree

    init {
        treeMap = tree.toTreeModel { it -> dimBuildingArea(it) }
        treeMap.layout(SquarifiedLayout(), Rect(-1000.0, -1000.0, WORLD_LENGTH, WORLD_LENGTH))
    }

    /** Calculate desired building area based on line count (building volume). */
    fun dimBuildingArea(file: RepoFile): Double {
        val volume = file.lineCount.toDouble()

        // Height assuming a cubic with 12 equals sides
        var height = Math.pow(volume, 1.0 / 3)

        // We have a semi-random maximum height per building
        val maxHeight = Math.ceil(BUILDING_MAX_HEIGHT - Math.floor(Math.random() * BUILDING_MAX_HEIGHT_VARIANCE))
        height = Math.min(height, maxHeight)

        // resulting ground area would be
        val area = volume / height

        // We have a fixed minimum ground area
        // area = max(1.0, area)

        // Add extra "area" for the surrounding street gap
        //area = pow(sqrt(area) + 2 * STREET_WIDTH, 2.0)

        return area
    }

    fun heightCorrectionFactor(): Double {
        // 1 line should be equal to 1x1x1
        return WORLD_LENGTH * WORLD_LENGTH / tree.lineCount
    }

}

fun RepoFile.toTreeModel(nodeSizer: (RepoFile) -> Double ) : TreeModel {
    val treeModel = TreeModel(MappableRepoFile(this, nodeSizer))

    val fileChildren = this.children
    if (fileChildren != null) {
        for (child in fileChildren.values) {
            if (child.lineCount > 0) {   // ignore empty files
                treeModel.addChild(TreeModel(MappableRepoFile(child, nodeSizer)))
            }
        }
    }

    return treeModel
}


class MappableRepoFile(val repoFile: RepoFile, sizer: (f: RepoFile) -> Double) : MapItem(sizer(repoFile)), Mappable { }

/** The length/width of the world box */
val WORLD_LENGTH = 2000.0