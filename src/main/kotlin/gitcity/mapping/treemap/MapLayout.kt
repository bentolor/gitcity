package gitcity.mapping.treemap

/** The interface for all gitcity.mapping.treemap layout algorithms. */
interface MapLayout {
    /**
     * Arrange the items in the given MapModel to fill the given rectangle.

     * @param model The MapModel.
     * @param bounds The boundsing rectangle for the layout.
     */
    fun layout(model: MapModel, bounds: Rect)

    fun layout(items: Array<Mappable>, bounds: Rect)
}
