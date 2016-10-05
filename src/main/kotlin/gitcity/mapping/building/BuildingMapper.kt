package gitcity.mapping.building

import gitcity.ChangeLogAnalysis
import gitcity.mapping.treemap.Rect
import gitcity.mapping.treemap.SquarifiedLayout
import gitcity.mapping.treemap.TreeModel
import gitcity.mapping.treemap.TreeModelVisitor
import gitcity.repository.RepoFile

/**
 * Various algorithms, settings and heuristics to influence the placement, sizing and other visual properties of the "buildings" in
 * GitCity representing the files.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class BuildingMapper(analysis: ChangeLogAnalysis, worldLength: Double = 30.0) {

    val treeMap: TreeModel
    val tree: RepoFile

    val sourceWorldVolume: Double
    val sourceWorldArea: Double

    init {
        tree = analysis.epochs.last().fileTree

        sourceWorldVolume = tree.lineCount.toDouble()
        sourceWorldArea = sourceWorldVolume / Math.pow(sourceWorldVolume, 1.0 / 3)

        treeMap = tree.toTreeModel(this)

        val worldRect = Rect(-worldLength / 2, -worldLength / 2, worldLength, worldLength)
        treeMap.mappable.bounds = worldRect

        treeMap.layout(SquarifiedLayout())

        treeMap.accept(PostProcessing())
    }

    inner class PostProcessing: TreeModelVisitor {
        override fun visit(model: TreeModel) {
            val mappableRepoFile = model.mappable as MappableRepoFile
            val props = mappableRepoFile.buildingProperties
            // Resize bounds to simulate surrounding streets
            // Shring by 20%, center in shrunken area
            with(mappableRepoFile.bounds) {
                x += 0.1 * w
                w *= 0.8
                y += 0.1 * h
                h *= 0.8
                // dim height
                props.targetHeight =  Math.pow(w * h, 1.0 / 2)
            }
        }
    }

    /** Calculate desired building area based on line count (building volume). */
    fun dimBuilding(file: RepoFile): BuildingProperties {
        val sourceVolume = file.lineCount.toDouble()

        // Height assuming a cubic with 12 equals sides
        val sourceHeight = Math.pow(sourceVolume, 1.0 / 3)

        // We have a semi-random maximum height per building
        //val maxHeight = Math.ceil(BUILDING_MAX_HEIGHT - Math.floor(Math.random() * BUILDING_MAX_HEIGHT_VARIANCE))
        //height = Math.min(height, maxHeight)

        // resulting ground area would be
        val sourceArea = sourceVolume / sourceHeight

        // We have a fixed minimum ground area
        // area = max(1.0, area)

        // Add extra "area" for the surrounding street gap
        //area = pow(sqrt(area) + 2 * STREET_WIDTH, 2.0)

        //if (file.isLeaf) {
        //    trace("\t${file.name}[${file.lineCount}] -> ${Math.round(area)} x ${Math.round(height)}")
        //}

        val relativeVolume = sourceVolume / sourceWorldVolume
        val relativeArea = sourceArea / this.sourceWorldArea

        return BuildingProperties(sourceArea, sourceHeight, relativeArea, relativeVolume)
    }

}

