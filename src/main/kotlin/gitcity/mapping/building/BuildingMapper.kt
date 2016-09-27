package gitcity.mapping.building

import gitcity.ChangeLogAnalysis
import gitcity.mapping.squarified.SquarifiedLayout
import gitcity.mapping.squarified.TreeNode
import gitcity.mapping.squarified.TreeNodeVisitor
import gitcity.repository.RepoFile
import gitcity.trace
import gitcity.warn

/**
 * Various algorithms, settings and heuristics to influence the placement, sizing and other visual properties of the "buildings" in
 * GitCity representing the files.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class BuildingMapper(analysis: ChangeLogAnalysis) {

    val treeMap: TreeNode
    val tree: RepoFile

    private val BUILDING_AVG_HEIGHT = 20

    val worldLength: Double
    val oneLineArea: Double
    val totalLOC: Double

    init {
        tree = analysis.epochs.last().fileTree

        totalLOC = tree.lineCount.toDouble()
        worldLength = dimWorldLength(totalLOC)
        oneLineArea = BUILDING_AVG_HEIGHT * worldLength * worldLength / totalLOC

        treeMap = tree.toTreeNode(this)
        /*treeMap.assertFileSizeSums()
        treeMap.assertSizes()
        treeMap.layout(/*SquarifiedLayout()*/SliceLayout(), Rect(w = worldLength, h = worldLength))*/
        SquarifiedLayout(-worldLength / 2.0, -worldLength / 2.0, worldLength, worldLength).layout(treeMap)
        treeMap.accept(AssertRelativeSizeVisitor())
    }

    inner class AssertRelativeSizeVisitor : TreeNodeVisitor {
        override fun visit(model: TreeNode) {
            val file = model.payload as RepoFile
            val lineCount = file.lineCount
            val areaRatio = worldLength * worldLength / model.area
            val locRatio = totalLOC / lineCount
            val absDiff = Math.abs(areaRatio - locRatio)
            if (absDiff > 0.0000001)
                warn("LOC to Area ratio mismatch: ${file.name} LOC: $locRatio vs. AREA: $areaRatio -- Lines: $lineCount ${model
                        .width}x${model.height} = ${model.area} ")
            else
                trace("LOC to Area ratio: ${file.name} LOC: $locRatio vs. AREA: $areaRatio")
        }
    }
    /*inner class AssertRelativeSizeVisitor : TreeModelVisitor {
        override fun visit(model: TreeModel) {
            val file = (model.mappable as MappableRepoFile).repoFile
            val lineCount = file.lineCount
            val areaRatio = worldLength * worldLength / model.mappable.bounds.area
            val locRatio = totalLOC / lineCount
            if (Math.abs(areaRatio - locRatio) > 0.0000001)
                warn("LOC to Area ratio mismatch: ${file.name} LOC: $locRatio vs. AREA: $areaRatio")
        }
    }*/

    /** Calculate desired building area based on line count (building volume). */
    fun dimBuildingArea(file: RepoFile): Double {
        val volume = file.lineCount.toDouble()

        // Height assuming a cubic with 12 equals sides
        //val height = Math.pow(volume, 1.0 / 3)
        val height = 1.0

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
            trace("\t${file.name}[${file.lineCount}] -> ${Math.round(area)} x ${Math.round(height)}")
        }

        return area
    }

    fun dimWorldLength(totalVolume: Double) = Math.sqrt(totalVolume / BUILDING_AVG_HEIGHT)

}

