package gitcity.mapping.building

import gitcity.ChangeLogAnalysis
import gitcity.info
import gitcity.mapping.treemap.Rect
import gitcity.mapping.treemap.SquarifiedLayout
import gitcity.mapping.treemap.TreeModel
import gitcity.repository.RepoFile
import gitcity.trace

/**
 * Various algorithms, settings and heuristics to influence the placement, sizing and other visual properties of the "buildings" in
 * GitCity representing the files.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class BuildingMapper(analysis: ChangeLogAnalysis) {

    val treeMap: TreeModel
    val tree: RepoFile

    private val BUILDING_MAX_HEIGHT = 120
    private val BUILDING_MAX_HEIGHT_VARIANCE = 20
    private val BUILDING_AVG_HEIGHT = 20

    private var worldLength: Double
    private var largestHeight: Double = 0.0

    init {
        tree = analysis.epochs.last().fileTree

        worldLength = dimWorldLength(tree.lineCount.toDouble())

        treeMap = tree.toTreeModel(this)
        treeMap.assertFileSizeSums()
        treeMap.assertSizes()

        treeMap.layout(SquarifiedLayout(), Rect(-worldLength / 2, -worldLength / 2, worldLength, worldLength))

        info("largest building: $largestHeight")
    }

    /** Calculate desired building area based on line count (building volume). */
    fun dimBuildingArea(file: RepoFile): Double {
        val volume = file.lineCount.toDouble()

        // Height assuming a cubic with 12 equals sides
        val height = Math.pow(volume, 1.0 / 3)

        // We have a semi-random maximum height per building
        //val maxHeight = Math.ceil(BUILDING_MAX_HEIGHT - Math.floor(Math.random() * BUILDING_MAX_HEIGHT_VARIANCE))
        //height = Math.min(height, maxHeight)

        // resulting ground area would be
        val area = volume / height

        // We have a fixed minimum ground area
        // area = max(1.0, area)

        // Add extra "area" for the surrounding street gap
        //area = pow(sqrt(area) + 2 * STREET_WIDTH, 2.0)

        trace("${file.name}: ${file.lineCount} -> ${area.toInt()} x ${height.toInt()}")

        if (file.children == null) {
            largestHeight = Math.max(largestHeight, height)
        }

        return area
    }

    fun dimWorldLength(totalVolume: Double) = Math.sqrt(totalVolume / BUILDING_AVG_HEIGHT)

}

