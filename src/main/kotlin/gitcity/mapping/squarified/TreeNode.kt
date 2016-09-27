package gitcity.mapping.squarified

import java.util.*

class TreeNode {

    internal var childNodes: MutableList<TreeNode>? = null
    internal var childNodesProcessed = false

    internal var width: Double = 0.toDouble()
    internal var height: Double = 0.toDouble()
    internal var targetArea: Double = 0.toDouble()

    //tmp_width  & tmp_height is used for back-tracking to previous value of width
    internal var tmp_width: Double = 0.toDouble()
    internal var tmp_height: Double = 0.toDouble()
    // last aspect ratio
    internal var aspectLast: Double = 0.toDouble()
    //Coordinates of the rectangle of given width and height
    internal var x: Double = 0.toDouble()
    internal var y: Double = 0.toDouble()

    internal val payload: Any?
    internal var payloadSize: Double = 0.toDouble()

    constructor(payloadSize: Double, payload: Any? = null) {
        this.payloadSize = payloadSize
        this.payload = payload
    }

    constructor(childrens: List<TreeNode>, payload: Any? = null) {
        this.childNodes = ArrayList(childrens)
        this.payload = payload
        if (childNodes != null) {
            payloadSize = 0.0
            for (childNode in childNodes!!) {
                payloadSize += childNode.payloadSize
            }
        }
    }

    internal val childNodesCount: Int
        get() = childNodes?.size ?: 0

    fun addChild(treeNode: TreeNode) {
        val childs = childNodes ?: mutableListOf()
        childs.add(treeNode)
        childNodes = childs
    }

    fun accept(visitor: TreeNodeVisitor) {
        visitor.visit(this)
        val childs = childNodes
        if (childs != null) {
            for (child in childs) {
                child.accept(visitor)
            }
        }
    }

    val isLeaf: Boolean
        get() = childNodes?.size == 0
}
