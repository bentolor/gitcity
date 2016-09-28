package gitcity.mapping.treemap

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Test functionality of treemapping layout.
 * @author Benjamin Schmid <benjamin.schmid@exxcellent.de>
 */
@RunWith(Parameterized::class)
class LayoutTest(private val layouter: MapLayout) {

    private val mapOfThree = listOf<Mappable>(MapItem(50.0), MapItem(25.0), MapItem(25.0))
    private val mapOfFour = listOf<Mappable>(MapItem(40.0), MapItem(20.0), MapItem(20.0), MapItem(20.0))
    private val factor = 3.14

    /**
     * Three segments, 2:1:1 on an area of 100. Expecting 50,25,25 sized tiles.
     */
    @Test
    fun testOneToOneMapping() {
        layouter.layout(mapOfThree.toTypedArray(), Rect(-5.0, -5.0, 10.0 * factor, 10.0))
        val sum = mapOfThree.sumByDouble { it.bounds.w * it.bounds.h }
        assertEquals(100.0 * factor, sum, 0.01)
        with(mapOfThree[0]) {
            assertEquals(50.0, size, 0.01)
            assertEquals(50.0 * factor, bounds.w * bounds.h, 0.01)
        }
        with(mapOfThree[1]) {
            assertEquals(25.0, size, 0.01)
            assertEquals(25.0 * factor, bounds.w * bounds.h, 0.01)
        }
        with(mapOfThree[2]) {
            assertEquals(25.0, size, 0.01)
            assertEquals(25.0 * factor, bounds.w * bounds.h, 0.01)
        }
    }

    /**
     * Four segments, 2:1:1:1 on an area of 100. Expecting 40,20,20,20 sized tiles.
     */
    @Test
    fun testOneToOneMappingOfFour() {
        layouter.layout(mapOfFour.toTypedArray(), Rect(-0.0, -0.0, 10.0, 10.0))
        val sum = mapOfFour.sumByDouble { it.bounds.w * it.bounds.h }
        assertEquals(100.0, sum, 0.01)
        mapOfFour.indices.forEach { i ->
            with(mapOfFour[i]) {
                val tileSize = if (i == 0) 40.0 else 20.0
                assertEquals(tileSize, size, 0.01)
                assertEquals(tileSize, bounds.w * bounds.h, 0.01)
            }
        }
    }

    companion object {
        @Parameterized.Parameters @JvmStatic
        fun getLayouts(): Collection<Array<Any>> {
            return listOf(
                    arrayOf<Any>(SliceLayout()),
                    arrayOf<Any>(SliceLayout(SliceLayout.BEST)),
                    arrayOf<Any>(SquarifiedLayout())
            )
        }
    }

}