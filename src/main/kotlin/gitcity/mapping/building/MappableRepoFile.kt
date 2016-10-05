package gitcity.mapping.building

import gitcity.mapping.treemap.MapItem
import gitcity.mapping.treemap.Mappable
import gitcity.repository.RepoFile

/**
 * A class mapping a "repository file" representation to a "treemap mappable item".
 */
class MappableRepoFile(val repoFile: RepoFile, val buildingProperties: BuildingProperties) : MapItem(buildingProperties.sourceArea), Mappable {}