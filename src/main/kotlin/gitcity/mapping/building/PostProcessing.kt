package gitcity.mapping.building

import gitcity.mapping.treemap.TreeModel
import gitcity.mapping.treemap.TreeModelVisitor
import java.awt.Color
import java.lang.Math.pow
import java.lang.Math.random

/**
 * Resizes & Shapes the final building properties
 */
class PostProcessing : TreeModelVisitor {
    val extensionColorMap: MutableMap<String, Color> = mutableMapOf()

    override fun visit(model: TreeModel) {
        val mappableRepoFile = model.mappable as MappableRepoFile
        val props = mappableRepoFile.buildingProperties

        with(mappableRepoFile.bounds) {
            // Resize bounds to simulate surrounding streets
            // Shring by 20%, center in shrunken area
            x += 0.1 * w
            w *= 0.8
            y += 0.1 * h
            h *= 0.8

            // dim height
            props.targetHeight = pow(w * h, 1.0 / 2)
        }

        // shuffle a colour by file extension
        val fileName = mappableRepoFile.repoFile.name
        val extension = fileName.substringAfterLast('.', "<none>")
        props.color = modifyColorIntensity(extensionColorMap.getOrPut(extension, { randomColor() }))
    }

    fun randomColor(): Color = Color.getHSBColor(random().toFloat(), 1.0f, 0.7f)

    fun modifyColorIntensity(source: Color): Color {
        val hsbVals = Color.RGBtoHSB(source.red, source.green, source.blue, null)
        val value = 0.2 + ((random() / 2) * (random() / 2))
        val brightness = 0.8 - 0.5 * random() * random()
        return Color.getHSBColor(hsbVals[0], value.toFloat(), brightness.toFloat())
    }
}