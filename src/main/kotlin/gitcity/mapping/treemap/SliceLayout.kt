/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.mapping.treemap

/**
 * The original slice-and-dice layout for treemaps.
 */
class SliceLayout constructor(private val orientation: Int = SliceLayout.ALTERNATE) : AbstractMapLayout() {

    public override fun layout(items: Array<Mappable>, bounds: Rect) {
        if (items.size == 0) {
            return
        }
        val o = orientation
        if (o == BEST) {
            layoutBest(items, 0, items.size - 1, bounds)
        } else if (o == ALTERNATE) {
            layout(items, bounds, items[0].depth % 2)
        } else {
            layout(items, bounds, o)
        }
    }

    companion object {

        private val BEST = 2
        private val ALTERNATE = 3

        fun layoutBest(items: Array<Mappable>, start: Int, end: Int, bounds: Rect, order: Int = ASCENDING) {
            AbstractMapLayout.sliceLayout(items, start, end, bounds,
                    if (bounds.w > bounds.h) AbstractMapLayout.HORIZONTAL else AbstractMapLayout.VERTICAL, order)
        }

        private fun layout(items: Array<Mappable>, bounds: Rect, orientation: Int) {
            AbstractMapLayout.sliceLayout(items, 0, items.size - 1, bounds, orientation)
        }
    }

}
