/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.mapping.treemap

/** Model object used by MapLayout to represent data for a gitcity.mapping.treemap. */
interface MapModel {

    /**
     * Get the list of items in this gitcity.model.

     * @return An array of the Mappable objects in this MapModel.
     */
    val items: Array<Mappable>
}
