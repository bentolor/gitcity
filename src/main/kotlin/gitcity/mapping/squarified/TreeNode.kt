package gitcity.mapping.squarified

import java.util.*

class TreeNode {

    internal var parentNode: TreeNode? = null
    internal var childNodes: MutableList<TreeNode>? = null
    internal var area: Double = 0.toDouble()
    internal var width: Double = 0.toDouble()
    //tmp_width is used for back-tracking to previous value of width
    internal var tmp_width: Double = 0.toDouble()
    internal var height: Double = 0.toDouble()
    internal var tmp_height: Double = 0.toDouble()
    // last aspect ratio
    internal var aspectLast: Double = 0.toDouble()
    //Coordinates of the rectangle of given width and height
    internal var x: Double = 0.toDouble()
    internal var y: Double = 0.toDouble()

    constructor(area: Double) {
        this.area = area
    }

    constructor(childrens: List<TreeNode>) {
        this.childNodes = ArrayList(childrens)
        if (childNodes != null) {
            area = 0.0
            for (childNode in childNodes!!) {
                area += childNode.area
            }
        }
    }

    internal val childNodesCount: Int
        get() = childNodes?.size ?: 0



}
