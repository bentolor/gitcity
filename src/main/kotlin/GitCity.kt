import gitcity.ChangeLogAnalysis
import gitcity.GitCityOptions
import gitcity.JsonWriter
import gitcity.info
import gitcity.mapping.building.BuildingMapper
import gitcity.repository.GitRepositoryReader
import spark.Spark.*

/**
 * GitCity: A 3D/VR based visualisation of file changes within a Git repository.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
fun main(args: Array<String>) {

    printLogo()
    val opts = GitCityOptions().parse(args)

    val changeLog = GitRepositoryReader().parseRepository(opts)
    val analysis = ChangeLogAnalysis(opts, changeLog)

    val buildingMapper = BuildingMapper(analysis)

    port(opts.port)
    info("Access http://localhost:${opts.port}/ with your browser or VR device to access GitCity")
    staticFiles.location("/gitcity-client")

    get("/city/latest", { req, res ->
        res.type("application/json")
        JsonWriter(buildingMapper, buildingMapper.epochIds.last())
                .writeTo(res.raw().outputStream)
    })

    get("/city/stats", { req, res ->
        res.type("application/json")
        "{ \"epochCount\": ${buildingMapper.epochIds.size}, \"worldLength\": ${buildingMapper.worldLength} } "
    })

    get("/city/byIndex/:index", { req, res ->
        res.type("application/json")
        val param = req.params(":index")
        val epochIndex: Int = param?.toInt() ?: throw IllegalArgumentException("Unknown index $param")
        JsonWriter(buildingMapper, buildingMapper.epochIds[epochIndex])
                .writeTo(res.raw().outputStream)
    })
}

private fun printLogo() {
    println("\n     _____ _ _   _____ _ _       \n" +
            "    |   __|_| |_|     |_| |_ _ _      GitCity\n" +
            "    |  |  | |  _|   --| |  _| | |     \n" +
            "    |_____|_|_| |_____|_|_| |_  |     A VR visualisation of Git file history changes\n" +
            "                            |___|     2016 @bentolor\n")
}