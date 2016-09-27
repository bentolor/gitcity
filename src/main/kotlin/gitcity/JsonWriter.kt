package gitcity

import gitcity.mapping.building.BuildingMapper
import gitcity.mapping.squarified.TreeNode
import gitcity.mapping.squarified.TreeNodeVisitor
import gitcity.repository.RepoFile
import java.io.OutputStream
import java.io.Writer

/** Render layout result to an output stream .*/
class JsonWriter(private val buildingMapper: BuildingMapper) {

    fun writeTo(output: OutputStream) {
        output.writer().use { writer ->
            writer.append("{ \"mapItems\" : [\n")
            buildingMapper.treeMap.accept(LeafNodeJsonWriter(writer))
            writer.append("\n] }\n")
        }
    }

    private inner class LeafNodeJsonWriter(val writer: Writer) : TreeNodeVisitor {
        var first = true

        override fun visit(model: TreeNode) {
            if (!model.isLeaf) return
            if (!first) writer.append(",\n")
            first = false
            val file = model.payload as RepoFile
            // Area is surrounding street gap + building area. Recover real building sizes
            val buildingWidth = model.width //- 2 * STREET_WIDTH
            val buildingLength = model.height //- 2 * STREET_WIDTH
            val buildingHeight =  20 // file / (buildingLength * buildingWidth)

            writer.append("{" +
                    // The client expects x/y in the center of the cube
                    "\"x\": ${model.x + 0.5 * model.width}," +
                    "\"z\": ${model.y + 0.5 * model.height}," +
                    "\"w\": $buildingWidth," +
                    "\"l\": $buildingLength," +
                    "\"h\": $buildingHeight," +
                    "\"v\": ${buildingHeight * buildingWidth * buildingLength}," +
                    "\"s\": ${file.lineCount}," +
                    "\"f\": \"${file.name}\"" +
                    "}")
        }
    }


/*
      private inner class LeafNodeJsonWriter(val writer: Writer) : TreeModelVisitor {
        var first = true

        override fun visit(model: TreeModel) {
            if (!model.isLeaf) return
            if (!first) writer.append(",\n")
            first = false
            val mi = model.mappable as MappableRepoFile
            // Area is surrounding street gap + building area. Recover real building sizes
            val buildingWidth = mi.bounds.w //- 2 * STREET_WIDTH
            val buildingLength = mi.bounds.h //- 2 * STREET_WIDTH
            val buildingHeight = mi.size / (buildingLength * buildingWidth)

//            val correctedArea = buildingWidth * buildingHeight * buildingMapper.oneLineArea
//            val lc = mi.repoFile.lineCount
//            if (Math.round(correctedArea - lc) > 0 )
//                throw IllegalStateException("Nope!")

            writer.append("{" +
                    // The client expects x/y in the center of the cube
                    "\"x\": ${mi.bounds.x + 0.5 * mi.bounds.w}," +
                    "\"z\": ${mi.bounds.y + 0.5 * mi.bounds.h}," +
                    "\"w\": $buildingWidth," +
                    "\"l\": $buildingLength," +
                    "\"h\": $buildingHeight," +
                    "\"v\": ${buildingHeight * buildingWidth * buildingLength}," +
                    "\"s\": ${mi.repoFile.lineCount}," +
                    "\"f\": \"${mi.repoFile.name}\"" +
                    "}")
        }
    }
*/
}