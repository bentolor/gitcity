package gitcity.mapping.building

import gitcity.mapping.treemap.TreeModel
import gitcity.repository.RepoFile

fun RepoFile.toTreeModel(buildingMapper: BuildingMapper) : TreeModel {

    val relativeAreaSize = buildingMapper.dimBuildingArea(this)
    val mappable = MappableRepoFile(this, relativeAreaSize)

    val treeModel = TreeModel(mappable)

    val fileChildren = this.children
    if (fileChildren != null) {
        for (child in fileChildren.values) {
            if (child.lineCount > 0) {   // ignore empty files
                treeModel.addChild(child.toTreeModel(buildingMapper))
            }
        }
    }

    return treeModel
}

/*fun RepoFile.toTreeNode(buildingMapper: BuildingMapper) : TreeNode {
    val buildingArea = buildingMapper.dimBuildingArea(this)
    val treeNode = TreeNode(buildingArea, this)

    val fileChildren = this.children
    if (fileChildren != null && fileChildren.size > 0) {
        treeNode.payloadSize = 0.0 // the area is the sum of all childrens
        for (child in fileChildren.values) {
            if (child.lineCount > 0) {   // ignore empty files
                val newChild = child.toTreeNode(buildingMapper)
                treeNode.payloadSize += newChild.payloadSize
                treeNode.addChild(newChild)
            }
        }
    }

    return treeNode
}*/