package gitcity

import gitcity.mapping.building.BuildingMapper
import gitcity.mapping.building.MappableRepoFile
import gitcity.mapping.treemap.TreeModel
import gitcity.mapping.treemap.TreeModelVisitor
import java.io.OutputStream
import java.io.Writer

/** Render layout result to an output stream .*/
class JsonWriter(private val buildingMapper: BuildingMapper, val epochId: String) {

    fun writeTo(output: OutputStream) {
        output.writer().use { writer ->
            writer.append("{ \"mapItems\" : [\n")
            buildingMapper.getTreeMapByEpoch(epochId).accept(LeafNodeJsonWriter(writer))
            writer.append("\n],\n")
            writer.append("\"worldLength\": ${buildingMapper.worldLength}")
            writer.append("}\n")
            writer.flush()
        }
    }

    private inner class LeafNodeJsonWriter(val writer: Writer) : TreeModelVisitor {
        var first = true

        override fun visit(model: TreeModel) {
            if (!model.isLeaf) return
            if (!first) writer.append(",\n")
            first = false
            val mi = model.mappable as MappableRepoFile

            val buildingWidth = mi.bounds.w
            val buildingLength = mi.bounds.h
            val props = mi.buildingProperties

            writer.append("\n\t{" +
                    // The client expects x/y in the center of the cube
                    "\"x\": ${mi.bounds.x + 0.5 * mi.bounds.w}," +
                    "\"z\": ${mi.bounds.y + 0.5 * mi.bounds.h}," +
                    "\"w\": $buildingWidth," +
                    "\"l\": $buildingLength," +
                    "\"h\": ${props.targetHeight}," +
                    // "\"v\": ${buildingHeight * buildingWidth * buildingLength}," +
                    "\"f\": { \"lc\": ${mi.repoFile.lineCount}," +
                    "\"n\": \"${jsonFilter(mi.repoFile.name)}\"}," +
                    "\"color\": \"#${Integer.toHexString(props.color.rgb).substring(2)}\"" +
                    "}")
        }
    }

    private val jsonFilteredChars = listOf('\\','"',' ','\'')

    /** Literals like "UseCase \303\234bersicht.uml" would break JSON otherwise. */
    private fun jsonFilter(text: String): String = text.filter { !jsonFilteredChars.contains(it) }

}