package gitcity.mapping.building

import gitcity.ChangeLogAnalysis
import gitcity.mapping.treemap.Rect
import gitcity.mapping.treemap.SquarifiedLayout
import gitcity.mapping.treemap.TreeModel
import gitcity.mapping.treemap.TreeModelVisitor
import gitcity.repository.RepoFile
import gitcity.trace
import gitcity.warn

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

    val worldLength: Double
    val oneLineArea: Double
    val totalLOC: Double

    init {
        tree = analysis.epochs.last().fileTree

        totalLOC = tree.lineCount.toDouble()
        worldLength = dimWorldLength(totalLOC)
        oneLineArea = BUILDING_AVG_HEIGHT * worldLength * worldLength / totalLOC

        treeMap = tree.toTreeModel(this)
        treeMap.assertFileSizeSums()
        treeMap.assertSizes()

        treeMap.layout(SquarifiedLayout(), Rect(w = worldLength, h = worldLength))
        treeMap.accept(AssertRelativeSizeVisitor())
    }

    inner class AssertRelativeSizeVisitor : TreeModelVisitor {
        override fun visit(model: TreeModel) {
            val file = (model.mappable as MappableRepoFile).repoFile
            val lineCount = file.lineCount
            val areaRatio = worldLength * worldLength / model.mappable.bounds.area
            val locRatio = totalLOC / lineCount
            if (Math.abs(areaRatio - locRatio) > 0.0000001)
                warn("LOC to Area ratio mismatch: ${file.name} LOC: $locRatio vs. AREA: $areaRatio")
        }
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

        if (file.isLeaf) {
            trace("${file.name}[${file.lineCount}] -> ${Math.round(area)} x ${Math.round(height)}")
        }

        return area
    }

    fun dimWorldLength(totalVolume: Double) = Math.sqrt(totalVolume / BUILDING_AVG_HEIGHT)

}

