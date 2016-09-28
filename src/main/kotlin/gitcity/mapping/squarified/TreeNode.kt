package gitcity.mapping.squarified

import java.util.*

class TreeNode {

    var childNodes: MutableList<TreeNode>? = null
    var childNodesProcessed = false

    var width: Double = 0.toDouble()
        set(value) {
            field = if (value >= 0.0) value else throw IllegalStateException("negative value: $value")
        }
    var height: Double = 0.toDouble()
        set(value) {
            field = if (value >= 0.0) value else throw IllegalStateException("negative value: $value")
        }
    var targetArea: Double = 0.toDouble()
        set(value) {
            field = if (value >= 0.0) value else throw IllegalStateException("negative value: $value")
        }

    //tmp_width  & tmp_height is used for back-tracking to previous value of width
    var tmp_width: Double = 0.toDouble()
        set(value) {
            field = if (value >= 0.0) value else throw IllegalStateException("negative value: $value")
        }
    var tmp_height: Double = 0.toDouble()
        set(value) {
            field = if (value >= 0.0) value else throw IllegalStateException("negative value: $value")
        }

    // last aspect ratio
    var aspectLast: Double = 0.toDouble()

    //Coordinates of the rectangle of given width and height
    var x: Double = 0.toDouble()
    var y: Double = 0.toDouble()

    val payload: Any?
    var payloadSize: Double
        set(value) { if (value >= 0.0) field = value else throw IllegalStateException("negative value") }

    constructor(payloadSize: Double, payload: Any? = null) {
        this.payloadSize = payloadSize
        this.payload = payload
    }

    constructor(childrens: List<TreeNode>, payload: Any? = null) {
        this.childNodes = ArrayList(childrens)
        this.payload = payload
        var totalSize = 0.0
        if (childNodes != null) {
            for (childNode in childNodes!!) {
                totalSize += childNode.payloadSize
            }
        }
        this.payloadSize = totalSize
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
        get() = childNodes?.size ?: 0 == 0
}
