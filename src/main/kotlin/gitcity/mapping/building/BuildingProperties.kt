package gitcity.mapping.building

data class BuildingProperties(val sourceArea: Double,
                              val sourceHeight: Double,
                              val relativeArea: Double,
                              val relativeSize: Double) {
    var targetHeight: Double = 0.0
    val sourceVolume: Double
        get() = sourceArea * sourceHeight
}