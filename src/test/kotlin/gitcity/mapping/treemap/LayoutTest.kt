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

    private val exampleMap = listOf<Mappable>(MapItem(50.0), MapItem(25.0), MapItem(25.0))

    /**
     * Three segments, 2:1:1 on an area of 100. Expecting 50,25,25 sized tiles.
     */
    @Test
    fun testOneToOneMapping() {
        layouter.layout(exampleMap.toTypedArray(), Rect(-5.0, -5.0, 10.0, 10.0))
        val sum = exampleMap.sumByDouble { it.bounds.w * it.bounds.h }
        assertEquals(100.0, sum, 0.01)
        with(exampleMap[1]) {
            assertEquals(25.0, size, 0.01)
            assertEquals(25.0, bounds.w * bounds.h, 0.01)
        }
        with(exampleMap[2]) {
            assertEquals(25.0, size, 0.01)
            assertEquals(25.0, bounds.w * bounds.h, 0.01)
        }
        with(exampleMap[0]) {
            assertEquals(50.0, size, 0.01)
            assertEquals(50.0, bounds.w * bounds.h, 0.01)
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