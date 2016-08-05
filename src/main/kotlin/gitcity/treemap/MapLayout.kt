package gitcity.treemap

/** The interface for all gitcity.treemap layout algorithms. */
interface MapLayout {
    /**
     * Arrange the items in the given MapModel to fill the given rectangle.

     * @param model The MapModel.
     * @param bounds The boundsing rectangle for the layout.
     */
    fun layout(model: MapModel, bounds: Rect)
}
