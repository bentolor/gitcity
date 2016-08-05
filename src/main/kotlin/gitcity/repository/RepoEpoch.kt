package gitcity.repository

import gitcity.repository.RepoFile

/**Data models to store the timeline gitcity.model */
data class  RepoEpoch(val changeSet: ChangeSet,
                      val fileTree: RepoFile)