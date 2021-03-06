/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.mapping.treemap

/** A simple implementation of the Mappable interface.  */
open class MapItem constructor(override var size: Double) : Mappable {
    override var bounds: Rect = Rect(0.0, 0.0, 0.0, 0.0)
}
