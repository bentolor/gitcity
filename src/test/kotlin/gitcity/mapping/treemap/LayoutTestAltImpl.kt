package altimpl

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test functionality of treemapping layout.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
class LayoutTestAltImpl() {

    private val mapOfThree = arrayOf(TreeNode(50.0), TreeNode(25.0), TreeNode(25.0))
    private val mapOfFour = arrayOf(TreeNode(55.0), TreeNode(15.0), TreeNode(15.0), TreeNode(15.0))

    /**
     * Three segments, 2:1:1 on an area of 100. Expecting 50,25,25 sized tiles.
     */
    @Test
    fun testOneToOneMapping() {
        val root = TreeNode(mapOfThree)
        SquarifiedLayout(0.0, 0.0, 5.0, 20.0).layout(root)
        val sum = root.childNodes.sumByDouble { it.width * it.height }
        assertEquals(100.0, sum, 0.01)
        with(mapOfThree[0]) {
            assertEquals(50.0, area, 0.01)
            assertEquals(50.0, width * height, 0.01)
        }
        with(mapOfThree[1]) {
            assertEquals(25.0, area, 0.01)
            assertEquals(25.0, width * height, 0.01)
        }
        with(mapOfThree[2]) {
            assertEquals(25.0, area, 0.01)
            assertEquals(25.0, width * height, 0.01)
        }
    }

    /**
     * Four segments, 2:1:1:1 on an area of 100. Expecting 40,20,20,20 sized tiles.
     */
    @Test
    fun testOneToOneMappingOfFour() {
        val root = TreeNode(mapOfFour)
        SquarifiedLayout(0.0, 0.0, 5.0, 20.0).layout(root)
        val sum = mapOfFour.sumByDouble { it.width * it.height  }
        assertEquals(100.0, sum, 0.01)
        mapOfFour.indices.forEach { i ->
            with(mapOfFour[i]) {
                val tileSize = if (i == 0) 55.0 else 15.0
                assertEquals(tileSize, area, 0.01)
                assertEquals(tileSize, width * height, 0.01)
            }
        }
    }

}