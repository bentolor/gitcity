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
class BuildingMapper(analysis: ChangeLogAnalysis, val worldLength: Double = 1000.0) {

    val treeMap: TreeModel
    val tree: RepoFile

    val totalLOC: Double

    init {
        tree = analysis.epochs.last().fileTree
        treeMap = tree.toTreeModel(this)

        totalLOC = tree.lineCount.toDouble()

        val worldRect = Rect(-worldLength / 2, -worldLength / 2, worldLength, worldLength)
        treeMap.mappable.bounds = worldRect

        treeMap.layout(SquarifiedLayout())
        treeMap.accept(AssertRelativeSizeVisitor())
    }

    inner class AssertRelativeSizeVisitor : TreeModelVisitor {
        val worldArea = Math.pow(worldLength, 2.0)

        override fun visit(model: TreeModel) {
            val file = (model.mappable as MappableRepoFile).repoFile

            val lineCount = file.lineCount
            val locRatio = totalLOC / lineCount

            val areaRatio = worldArea / model.mappable.bounds.area

            if (Math.abs(areaRatio - locRatio) > 0.0000001)
                warn("LOC to Area ratio mismatch: ${file.name} LOC: $locRatio vs. AREA: $areaRatio")
        }
    }

    /** Calculate desired building area based on line count (building volume). */
    fun dimBuildingArea(file: RepoFile): Double {
        val volume = file.lineCount.toDouble()

        // Height assuming a cubic with 12 equals sides
        val height = 1.0 //  Math.pow(volume, 1.0 / 3)

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

}

