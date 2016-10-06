package gitcity.mapping.building

import java.awt.Color

data class BuildingProperties(val sourceArea: Double,
                              val sourceHeight: Double,
                              val relativeArea: Double,
                              val relativeSize: Double,
                              var targetHeight: Double = 0.0,
                              var color: Color = Color.RED) {
}