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

    /*constructor(r: Rect) {
        setRect(r.x, r.y, r.w, r.h)
    }*/

    constructor(x: Double = 0.0, y: Double = 0.0, w: Double = 1.0, h: Double = 1.0) {
        setRect(x, y, w, h)
    }

    fun setRect(x: Double, y: Double, w: Double, h: Double) {
        this.x = x
        this.y = y
        this.w = w
        this.h = h
    }


    /*fun aspectRatio(): Double {
        return Math.max(w / h, h / w)
    }

    fun distance(r: Rect): Double {
        return Math.sqrt(
                (r.x - x) * (r.x - x) +
                (r.y - y) * (r.y - y) +
                (r.w - w) * (r.w - w) +
                (r.h - h) * (r.h - h)
        )
    }

    fun copy(): Rect {
        return Rect(x, y, w, h)
    }*/

    override fun toString(): String {
        return "Rect: $x, $y, $w, $h"
    }

}
