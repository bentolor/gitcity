package gitcity.mapping.building

import gitcity.mapping.treemap.TreeModel
import gitcity.repository.RepoFile
import gitcity.trace

fun RepoFile.toTreeModel(buildingMapper: BuildingMapper) : TreeModel {

    val mappable = MappableRepoFile(this, buildingMapper.dimBuilding(this))

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

fun RepoFile.buildTreeModelByTemplate(template: TreeModel, buildingMapper: BuildingMapper) : TreeModel {

    val sourceFile = template.mappable as MappableRepoFile
    val locRatio = this.lineCount.toDouble() / sourceFile.repoFile.lineCount
    val templateTargetHeight = sourceFile.buildingProperties.targetHeight
    val templateHeight = sourceFile.buildingProperties.sourceHeight
    val newTargetHeight = templateTargetHeight * locRatio
    val newSourceHeight = templateHeight * locRatio

    val newProps = sourceFile.buildingProperties.copy(sourceHeight = newSourceHeight, targetHeight = newTargetHeight)
    val newCopy = TreeModel(MappableRepoFile(this, newProps))
    newCopy.mappable.bounds = template.mappable.bounds.copy()
    newCopy.mappable.size = template.mappable.size

    val fileChildren = this.children
    if (fileChildren != null) {
        for (child in fileChildren.values) {
            if (child.lineCount > 0) {   // ignore empty files
                val childTemplate = template.childByFilename(child.name)
                if (childTemplate != null)
                    newCopy.addChild(child.buildTreeModelByTemplate(childTemplate, buildingMapper))
                else
                    trace("File ${child.path} seems to have vanished during its lifetime")
            }
        }
    }

    return newCopy
}

fun TreeModel.childByFilename(fileName: String): TreeModel? =
    children.firstOrNull() { (it.mappable as MappableRepoFile).repoFile.name == fileName   }
