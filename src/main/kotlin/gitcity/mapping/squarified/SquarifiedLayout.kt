package gitcity.mapping.squarified

class SquarifiedLayout(private val worldOffsetX: Double,
                       private val worldOffsetY: Double,
                       private val worldWidth: Double,
                       private val worldHeight: Double) {


    private var previousOrientation: Orientation? = null
    private var previousIndex: Int = 0                    //Index value of previous operation..initialised to 0
    private var currentIndex: Int = 0
    private var end: Int = 0
    private var currentWidth: Double = 0.toDouble()
    private var currentHeight: Double = 0.toDouble()

    private fun preorder(node: TreeNode) {        //To traverse the tree in preorder way of traversal
        val nodeChilds = node.childNodes
        if (nodeChilds != null) {
            currentWidth = node.width
            currentHeight = node.height
            while (end != nodeChilds.size) {
                squarify(node, nodeChilds)            //squarify function finds the squarified treemap of this node
            }
            for (i in 0..nodeChilds.size - 1) {
                preorder(nodeChilds[i])
            }
        }
        //else Leaf node!
    }

    private fun calculateAspectRatio(height: Double, width: Double): Double {    //gets the aspect ratio of a node
        return Math.max(height / width, width / height)
    }

    //checks if nodes are to be drawn vertically or horizontally (if return value is true then, vertically)
    private fun calculateOrientation(width: Double, height: Double): Orientation {
        return if (width > height) Orientation.LANDSCAPE else Orientation.PORTRAIT
    }

    private enum class Orientation {
        LANDSCAPE, PORTRAIT
    }

    //find the aspect ratio which is more closer to 1 than the other
    private fun compareAspect(child: List<TreeNode>, end: Int, aspectCurr: Double): Boolean {
        return Math.abs(aspectCurr - 1) > Math.abs(child[end].aspectLast - 1)
    }

    private fun squarify(node: TreeNode, nodeChildren: List<TreeNode>) {
        val orientation = calculateOrientation(currentWidth, currentHeight)
        end = currentIndex
        var sum: Double
        var aspectCurr = java.lang.Double.MAX_VALUE               //setting aspectCurr to maximum value possible helpful for back-tracking
        do {
            if (currentIndex != (node.childNodes?.size ?: 0) - 1) {                      //as last item has to placed in the remaining area
                var totalArea = 0.0
                for (t in currentIndex..end) {
                    totalArea += nodeChildren[t].area
                }
                for (i in currentIndex..end) {
                    if (orientation == Orientation.PORTRAIT) {
                        nodeChildren[i].width = totalArea / currentHeight
                        nodeChildren[i].height = nodeChildren[i].area / nodeChildren[i].width
                    } else {
                        nodeChildren[i].height = totalArea / currentWidth
                        nodeChildren[i].width = nodeChildren[i].area / nodeChildren[i].height
                    }
                }
                //finding aspect ratio of last item
                nodeChildren[end].aspectLast = calculateAspectRatio(nodeChildren[end].height, nodeChildren[end].width)
            }
            //here again last item has no need to be compared
            val lastNode = currentIndex != node.childNodesCount - 1
            val lastInSegment = end != node.childNodesCount - 1
            if (compareAspect(nodeChildren, end, aspectCurr) && lastNode && lastInSegment) {
                //Aspect ratio is closer to 1 ! Adding next item.....
                aspectCurr = nodeChildren[end].aspectLast
                for (i in currentIndex..end) {
                    nodeChildren[i].tmp_height = nodeChildren[i].height
                    nodeChildren[i].tmp_width = nodeChildren[i].width
                }
                end++ // add next item

            } else {
                //remove that item

                if (currentIndex == node.childNodesCount - 1) {
                    //incrementing end value for last item
                    end++
                }
                if (end == node.childNodesCount - 1) {
                    if (compareAspect(nodeChildren, end, aspectCurr)) {
                        end++
                    }
                }
                for (j in currentIndex..end - 1) {
                    if (currentIndex == node.childNodesCount - 1) {                                                //for last item
                        nodeChildren[j].height = currentHeight
                        nodeChildren[j].width = currentWidth
                    } else {
                        if (end != node.childNodesCount) {
                            nodeChildren[j].height = nodeChildren[j].tmp_height
                            nodeChildren[j].width = nodeChildren[j].tmp_width
                        }
                    }
                    nodeChildren[j].aspectLast = calculateAspectRatio(nodeChildren[j].height, nodeChildren[j].width)
                    if (j == 0) {                                                            //find coordinates of first item
                        nodeChildren[j].x = worldOffsetX
                        nodeChildren[j].y = worldOffsetY
                    } else if (j == currentIndex) {
                        if (previousOrientation == Orientation.PORTRAIT) {
                            nodeChildren[currentIndex].x = nodeChildren[previousIndex].x + nodeChildren[previousIndex].width
                            nodeChildren[currentIndex].y = nodeChildren[previousIndex].y
                        } else {
                            nodeChildren[currentIndex].y = nodeChildren[previousIndex].y + nodeChildren[previousIndex].height
                            nodeChildren[currentIndex].x = nodeChildren[previousIndex].x
                        }
                        previousOrientation = orientation
                        previousIndex = currentIndex
                    } else {
                        sum = 0.0
                        if (orientation == Orientation.PORTRAIT) {
                            for (k in currentIndex..j - 1) {
                                sum += nodeChildren[k].height
                            }
                            nodeChildren[j].x = nodeChildren[currentIndex].x
                            nodeChildren[j].y = nodeChildren[currentIndex].y + sum
                        } else {
                            for (k in currentIndex..j - 1) {
                                sum += nodeChildren[k].width
                            }
                            nodeChildren[j].y = nodeChildren[currentIndex].y
                            nodeChildren[j].x = nodeChildren[currentIndex].x + sum
                        }
                    }

                }
                if (end != node.childNodesCount) {
                    if (orientation == Orientation.PORTRAIT) {
                        currentWidth -= nodeChildren[currentIndex].width
                    } else {
                        currentHeight -= nodeChildren[currentIndex].height
                    }
                    currentIndex = end
                    return
                }
                break
            }
        } while (end != node.childNodesCount)

    }

    fun layout(root: TreeNode): TreeNode {
        previousIndex = 0
        currentIndex = 0
        end = 0

        root.parentNode = null
        root.height = worldHeight
        root.width = worldWidth
        previousOrientation = calculateOrientation(worldWidth, worldHeight)

        val number = root.childNodesCount

        var totalSum = 0.0
        val childNodes = root.childNodes
        if (childNodes != null) {
            for (j in 0..root.childNodesCount - 1) {
                totalSum += childNodes[j].area
            }
            val ratio = root.area / totalSum
            for (i in 0..number - 1) {
                childNodes[i].area = childNodes[i].area * ratio
                childNodes[i].parentNode = root
                childNodes[i].childNodes = null
                childNodes[i] = childNodes[i]
            }

            preorder(root)
        }
        return root
    }

}