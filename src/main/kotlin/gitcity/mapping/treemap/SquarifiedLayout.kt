/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.mapping.treemap

/** "Squarified" gitcity.mapping.treemap layout invented by J.J. van Wijk */
class SquarifiedLayout : AbstractMapLayout() {

    override fun layout(items: Array<Mappable>, bounds: Rect) {
        layout(sortDescending(items), 0, items.size - 1, bounds)
    }

    private fun layout(items: Array<Mappable>, start: Int, end: Int, bounds: Rect) {
        if (start > end) {
            return
        }

        if (end - start < 2) {
            SliceLayout.layoutBest(items, start, end, bounds)
            return
        }

        val x = bounds.x
        val y = bounds.y
        val w = bounds.w
        val h = bounds.h

        val total = sum(items, start, end)
        var mid = start
        val a = items[start].size / total
        var b = a

        if (w < h) {
            // height/width
            while (mid </*=*/ end) {
                val aspect = normAspect(h, w, a, b)
                val q = items[mid].size / total
                if (normAspect(h, w, a, b + q) > aspect) {
                    break
                }
                mid++
                b += q
            }
            SliceLayout.layoutBest(items, start, mid, Rect(x, y, w, h * b))
            layout(items, mid + 1, end, Rect(x, y + h * b, w, h * (1 - b)))
        } else {
            // width/height
            while (mid </*=*/ end) {
                val aspect = normAspect(w, h, a, b)
                val q = items[mid].size / total
                if (normAspect(w, h, a, b + q) > aspect) {
                    break
                }
                mid++
                b += q
            }
            SliceLayout.layoutBest(items, start, mid, Rect(x, y, w * b, h))
            layout(items, mid + 1, end, Rect(x + w * b, y, w * (1 - b), h))
        }

    }

    private fun aspect(big: Double, small: Double, a: Double, b: Double): Double {
        return big * b / (small * a / b)
    }

    private fun normAspect(big: Double, small: Double, a: Double, b: Double): Double {
        val x = aspect(big, small, a, b)
        if (x < 1) {
            return 1 / x
        }
        return x
    }

    private fun sum(items: Array<Mappable>, start: Int, end: Int): Double {
        var s = 0.0
        for (i in start..end) {
            s += items[i].size
        }
        return s
    }


}
