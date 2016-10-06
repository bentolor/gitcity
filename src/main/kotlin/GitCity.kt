import gitcity.ChangeLogAnalysis
import gitcity.GitCityOptions
import gitcity.JsonWriter
import gitcity.info
import gitcity.mapping.building.BuildingMapper
import gitcity.repository.ChangeLog
import gitcity.repository.GitRepositoryReader
import spark.Spark.*

/**
 * GitCity: A 3D/VR based visualisation of file changes within a Git repository.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
fun main(args: Array<String>) {

    printLogo()
    val opts = GitCityOptions().parse(args)

    val changeLog: ChangeLog
    if (opts.gitLog == null) changeLog = GitRepositoryReader().parseRepository(opts)
    else changeLog = GitRepositoryReader().parseGitLogFile(opts)

    val analysis = ChangeLogAnalysis(opts, changeLog)
    val buildingMapper = BuildingMapper(analysis)

    info("Configuring web server")

    showUrlParameteHelp(opts)

    port(opts.port)
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

private fun showUrlParameteHelp(opts: GitCityOptions) {
    info("")
    info("Access http://localhost:${opts.port}/ with your browser or VR device to access GitCity")
    info("  The URL support options as query parameters:")
    info("      static = <true|false>    directly jump to the finished city w/o animation ")
    info("      frameDuration = <500>    ms between each frame ")
    info("      limitFrameCount = <200>  Max. number of frames you want to see.  ")
    info("")
    info("  Example:  http://localhost:${opts.port}/?static=false&frameDuration=300&limitFrameCount=40 ")
    info("")
}

private fun printLogo() {
    println("~".repeat(100))
    println("     _____ _ _   _____ _ _       \n" +
            "    |   __|_| |_|     |_| |_ _ _      GitCity\n" +
            "    |  |  | |  _|   --| |  _| | |     \n" +
            "    |_____|_|_| |_____|_|_| |_  |     A VR visualisation of Git file history changes\n" +
            "                            |___|     2016 @bentolor\n")
    println("\n   Start with --help for help on usage")
    println("~".repeat(100))
}