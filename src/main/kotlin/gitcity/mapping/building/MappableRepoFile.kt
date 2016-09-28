package gitcity.mapping.building

import gitcity.mapping.treemap.MapItem
import gitcity.mapping.treemap.Mappable
import gitcity.repository.RepoFile

/**
 * A class mapping a "repository file" representation to a "treemap mappable item".
 * @param sizer The sizer which converts the file into a map item size
 */
class MappableRepoFile(val repoFile: RepoFile, relativeAreaSize: Double) : MapItem(relativeAreaSize), Mappable {}