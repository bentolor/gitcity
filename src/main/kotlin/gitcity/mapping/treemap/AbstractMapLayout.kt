package gitcity.mapping.treemap

/**
 * Abstract class holding utility routines that several
 * implementations of MapLayout use.
 */
abstract class AbstractMapLayout : MapLayout {

    /** Subclasses implement this method themselves.  */
    override abstract fun layout(items: Array<Mappable>, bounds: Rect)

    override fun layout(model: MapModel, bounds: Rect) {
        layout(model.items, bounds)
    }

    // For a production system, use a quicksort...
    internal fun sortDescending(items: Array<Mappable>): Array<Mappable> {
        val s = arrayOf(*items)
        val n = s.size
        var outOfOrder = true
        while (outOfOrder) {
            outOfOrder = false
            for (i in 0..n - 1 - 1) {
                val wrong = s[i].size < s[i + 1].size
                if (wrong) {
                    val temp = s[i]
                    s[i] = s[i + 1]
                    s[i + 1] = temp
                    outOfOrder = true
                }
            }
        }
        return s
    }

    companion object {

        // Flags for type of rectangle division
        // and sort orders.
        val VERTICAL = 0
        val HORIZONTAL = 1
        val ASCENDING = 0

        private fun totalSize(items: Array<Mappable>, start: Int, end: Int): Double {
            var sum = 0.0
            for (i in start..end) {
                sum += items[i].size
            }
            return sum
        }

        internal fun sliceLayout(items: Array<Mappable>, start: Int, end: Int, bounds: Rect, orientation: Int, order: Int = ASCENDING) {
            val total = totalSize(items, start, end)
            var a = 0.0
            val vertical = orientation == VERTICAL

            for (i in start..end) {
                val r = Rect()
                val b = items[i].size / total
                if (vertical) {
                    r.x = bounds.x
                    r.w = bounds.w
                    if (order == ASCENDING) {
                        r.y = bounds.y + bounds.h * a
                    } else {
                        r.y = bounds.y + bounds.h * (1.0 - a - b)
                    }
                    r.h = bounds.h * b
                } else {
                    if (order == ASCENDING) {
                        r.x = bounds.x + bounds.w * a
                    } else {
                        r.x = bounds.x + bounds.w * (1.0 - a - b)
                    }
                    r.w = bounds.w * b
                    r.y = bounds.y
                    r.h = bounds.h
                }
                items[i].bounds = r
                a += b
            }
        }
    }
}
