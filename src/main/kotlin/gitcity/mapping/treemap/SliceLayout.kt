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
class SliceLayout constructor() : AbstractMapLayout() {

    override fun layout(items: Array<Mappable>, bounds: Rect) {
        if (items.size > 0) {
            layoutBest(items, 0, items.size - 1, bounds)
        }
    }

    fun layoutBest(items: Array<Mappable>, start: Int, end: Int, bounds: Rect, order: Order = Order.ASCENDING) {
        val bestOrientation = if (bounds.w > bounds.h) Orientation.HORIZONTAL else Orientation.VERTICAL
        sliceLayout(items, start, end, bounds, bestOrientation, order)
    }

}
