package gitcity.json

import gitcity.TreeModel
import gitcity.TreeeModelVisitor
import gitcity.buildingmap.MappableRepoFile
import java.io.OutputStream
import java.io.Writer

/** Render layout result to an output stream .*/
class JsonWriter(private val treeModel: TreeModel) {

    fun writeTo(output: OutputStream) {
        output.writer().use { writer ->
            writer.append("{ \"mapItems\" : [\n")
            treeModel.accept(LeafNodeJsonWriter(writer))
            writer.append("\n] }\n")
        }
    }

    private inner class LeafNodeJsonWriter(val writer: Writer) : TreeeModelVisitor {
        var first = true

        override fun visit(model: TreeModel) {
            if (model.children.isNotEmpty()) return
            if (!first) writer.append(",\n")
            first = false
            val mi = model.mappable as MappableRepoFile
            // Area is surrounding street gap + building area. Recover real building sizes
            val buildingWidth = mi.bounds.w //- 2 * STREET_WIDTH
            val buildingLength = mi.bounds.h //- 2 * STREET_WIDTH
            val buildingHeight = mi.repoFile.lineCount / (buildingLength * buildingWidth)

            writer.append("{" +
                    // The client expects x/y in the center of the cube
                    "\"x\": ${mi.bounds.x + 0.5 * mi.bounds.w}," +
                    "\"z\": ${mi.bounds.y + 0.5 * mi.bounds.h}," +
                    "\"w\": $buildingWidth," +
                    "\"l\": $buildingLength," +
                    "\"h\": $buildingHeight," +
                    "\"s\": ${mi.repoFile.lineCount}," +
                    "\"f\": \"${mi.repoFile.name}\"" +
                    "}")
        }
    }
}