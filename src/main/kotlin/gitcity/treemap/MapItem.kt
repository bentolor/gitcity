/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.treemap

/** A simple implementation of the Mappable interface.  */
open class MapItem constructor(size: Double = 1.0, override var order: Int = 0) : Mappable {
    override var bounds: Rect = Rect(0.0, 0.0, 0.0, 0.0)
    override var depth: Int = 0
    override var size: Double = size
    set(value) {
        if (value < 1)
            throw IllegalStateException("Size is < 1")
    }

    override fun setBounds(x: Double, y: Double, w: Double, h: Double) {
        bounds.setRect(x, y, w, h)
    }
}
