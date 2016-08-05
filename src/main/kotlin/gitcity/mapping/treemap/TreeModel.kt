/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.mapping.treemap

/**
 * An implementation of MapModel that represents  a hierarchical structure. It currently cannot  handle structural changes to the tree,
 * since it caches a fair amount of information.
 */
class TreeModel(val mappable: Mappable) : MapModel {
    val children = mutableListOf<TreeModel>()

    private var childItems: Array<Mappable>? = null
    //private var cachedTreeItems: List<Mappable>? = null
    private var parent: TreeModel? = null
        set(parent) {
            var p: TreeModel? = parent
            while (p != null) {
                if (p === this) {
                    throw IllegalArgumentException("Circular ancestry!")
                }
                p = p.parent
            }
            field = parent
        }

    private fun depth(): Int {
        if (this.parent == null) {
            return 0
        }
        return 1 + this.parent!!.depth()
    }

    private fun layout(tiling: MapLayout) {
        layout(tiling, mappable.bounds)
    }

    fun layout(tiling: MapLayout, bounds: Rect) {
        mappable.bounds = bounds
        if (children.isEmpty()) {
            return
        }
        setSums()
        tiling.layout(this, bounds)
        for (i in children.size - 1 downTo 0) {
            children[i].layout(tiling)
        }
    }

    /*private fun addTreeItems(v: MutableList<Mappable>) {
        if (children.isEmpty()) {
            v.add(mappable)
        } else {
            for (i in children.size - 1 downTo 0) {
                children[i].addTreeItems(v)
            }
        }
    }*/

    private fun setSums(): Double {
        val s = mappable.size
        /*
        var s = 0
        for (i in childCount() - 1 downTo 0) {
            s += getChild(i).setSums()
        }
        mappable.size = s*/
        return s
    }

    override val items: Array<Mappable>
        get() {
            val ci = childItems
            if (ci != null) {
                return ci
            } else {
                val n = children.size
                val cil = mutableListOf<Mappable>()
                for (i in 0..n - 1) {
                    val c = children[i].mappable
                    c.depth = 1 + depth()
                    cil.add(c)
                }
                childItems = cil.toTypedArray()
                return cil.toTypedArray()
            }
        }

    fun addChild(child: TreeModel) {
        child.parent = this
        children.add(child)
        childItems = null
    }

    fun accept(visitor: TreeModelVisitor) {
        visitor.visit(this)
        for (child in children) {
            child.accept(visitor)
        }
    }

}