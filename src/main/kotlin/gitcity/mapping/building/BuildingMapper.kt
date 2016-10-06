package gitcity.mapping.building

import gitcity.ChangeLogAnalysis
import gitcity.mapping.treemap.Rect
import gitcity.mapping.treemap.SquarifiedLayout
import gitcity.mapping.treemap.TreeModel
import gitcity.mapping.treemap.TreeModelVisitor
import gitcity.repository.RepoFile
import java.awt.Color

/**
 * Various algorithms, settings and heuristics to influence the placement, sizing and other visual properties of the "buildings" in
 * GitCity representing the files.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class BuildingMapper(analysis: ChangeLogAnalysis, val worldLength: Double = 30.0) {

    val treeMap: MutableMap<String, TreeModel>
    val epochIds: List<String>

    val sourceWorldVolume: Double
    val sourceWorldArea: Double

    init {
        epochIds = analysis.epochs.map { it.changeSet.id }

        val tree = analysis.epochs.last().fileTree

        sourceWorldVolume = tree.lineCount.toDouble()
        sourceWorldArea = sourceWorldVolume / Math.pow(sourceWorldVolume, 1.0 / 3)

        val worldRect = Rect(-worldLength / 2, -worldLength / 2, worldLength, worldLength)

        // We build the "final built" city based on the very last commit
        val finishedCity = buildFinalCity(tree, worldRect)
        treeMap = mutableMapOf(Pair(epochIds.last(), finishedCity))
    }

    private fun buildFinalCity(tree: RepoFile, worldRect: Rect): TreeModel {
        val lastEpochTreeMap = tree.toTreeModel(this)
        lastEpochTreeMap.mappable.bounds = worldRect
        lastEpochTreeMap.layout(SquarifiedLayout())
        lastEpochTreeMap.accept(PostProcessing())
        return lastEpochTreeMap
    }

    inner class PostProcessing : TreeModelVisitor {
        val extensionColorMap: MutableMap<String, Color> = mutableMapOf()

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
                props.targetHeight = Math.pow(w * h, 1.0 / 2)
            }

            // shuffle a colour by file extension
            val fileName = mappableRepoFile.repoFile.name
            val extension = fileName.substringAfterLast('.', "<none>")
            props.color = modifyColorIntensity(extensionColorMap.getOrPut(extension, { randomColor() }))
        }

        fun randomColor(): Color {
            val value = 0.15 + ((Math.random() / 2) * (Math.random() / 2))
            val brightness = 0.8 - 0.5 * Math.random() * Math.random()
            val hue = Math.random()
            return Color.getHSBColor(hue.toFloat(), value.toFloat(), brightness.toFloat())
        }

        fun modifyColorIntensity(source: Color): Color {
            val hsbVals = Color.RGBtoHSB(source.red, source.green, source.blue, null)
            val value = 0.15 + ((Math.random() / 2) * (Math.random() / 2))
            val brightness = 0.8 - 0.5 * Math.random() * Math.random()
            return Color.getHSBColor(hsbVals[0], value.toFloat(), brightness.toFloat())
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

