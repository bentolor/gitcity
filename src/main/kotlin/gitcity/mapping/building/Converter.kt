package gitcity.mapping.building

import gitcity.mapping.treemap.TreeModel
import gitcity.repository.RepoFile

fun RepoFile.toTreeModel(buildingMapper: BuildingMapper) : TreeModel {
    val treeModel = TreeModel(MappableRepoFile(this, { it -> buildingMapper.dimBuildingArea(it) }))

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