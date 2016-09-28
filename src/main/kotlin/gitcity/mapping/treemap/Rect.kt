/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.mapping.treemap

/**
 * A JDK 1.0 - compatible rectangle class that accepts double-valued parameters.
 */
class Rect {

    var x: Double = 0.0
    var y: Double = 0.0
    var w: Double = 0.0
    var h: Double = 0.0
    val area: Double
        get() = w * h

    constructor(x: Double = 0.0, y: Double = 0.0, w: Double = 0.0, h: Double = 0.0) {
        this.x = x
        this.y = y
        this.w = w
        this.h = h
    }

    override fun toString(): String {
        return "Rect: $x, $y, $w, $h"
    }

}
