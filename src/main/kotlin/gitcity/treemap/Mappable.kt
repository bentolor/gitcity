/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.treemap

/** Interface representing an object that can be placed in a gitcity.treemap layout.*/
interface Mappable {
    /** Corresponds to area in map. */
    var size: Double
    /** The bounding rectangle of the item in the map. */
    var bounds: Rect
    /** The sort order of the item. */
    var order: Int
    /** The depth in hierarchy. */
    var depth: Int

    fun setBounds(x: Double, y: Double, w: Double, h: Double)
}
