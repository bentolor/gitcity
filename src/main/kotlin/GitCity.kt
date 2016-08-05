
import gitcity.ChangeLogAnalysis
import gitcity.repository.GitRepositoryReader
import gitcity.mapping.building.BuildingMapper
import gitcity.JsonWriter
import gitcity.GitCityOptions
import spark.Spark.*

/**
 * GitCity: A 3D/VR based visualisation of file changes within a Git repository.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
fun main(args: Array<String>) {

    val opts = GitCityOptions().parse(args)

    val changeLog = GitRepositoryReader().parseRepository(opts)
    val analysis = ChangeLogAnalysis(opts, changeLog)

    val buildingMapper = BuildingMapper(analysis)

    port(8080)
    staticFiles.location("/gitcity-client")
    get("/items/latest", { req, res ->
        res.type("application/json")
        JsonWriter(buildingMapper.treeMap).writeTo(res.raw().outputStream)
    })
}