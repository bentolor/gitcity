/*
  Copyright (C) 2001 by University of Maryland, College Park, MD 20742, USA
  and Martin Wattenberg, w@bewitched.com
  All rights reserved.
  Authors: Benjamin B. Bederson and Martin Wattenberg
  http://www.cs.umd.edu/hcil/treemaps
 */

package gitcity.mapping.treemap

import gitcity.mapping.building.MappableRepoFile

/**
 * An implementation of MapModel that represents  a hierarchical structure. It currently cannot  handle structural changes to the tree,
 * since it caches a fair amount of information.
 */
class TreeModel(val mappable: Mappable) : MapModel {

    val children = mutableListOf<TreeModel>()
    val isLeaf: Boolean
        get() = children.isEmpty()
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

    private var childItems: Array<Mappable>? = null
    private var parent: TreeModel? = null
        set(parent) {
            var p = parent
            while (p != null) {
                if (p === this) throw IllegalArgumentException("Circular ancestry!")
                p = p.parent
            }
            field = parent
        }

    private fun setSums(): Double {
        if (isLeaf) return mappable.size
        else {
            val sum = children.fold(0.0, { s, tm -> s + tm.mappable.size })
            //mappable.size = sum
            return sum
        }
    }


    fun assertFileSizeSums() {
        if (isLeaf) return
        val sum = children.fold(0, { s, tm -> s + (tm.mappable as MappableRepoFile).repoFile.lineCount })
        if (sum != (mappable as MappableRepoFile).repoFile.lineCount)
            throw IllegalStateException("Line count mismatch")
        children.forEach { it.assertFileSizeSums() }
    }

    fun assertSizes() {
        children.forEach { it.assertSizes() }
        val sum = children.fold(0.0, { s, tm -> s + tm.mappable.size })
        if (!isLeaf && Math.abs(sum - mappable.size) > 0.000000001)
            throw IllegalStateException("Size mismatch")
    }

    fun layout(tiling: MapLayout, bounds: Rect) {
        mappable.bounds = bounds
        if (children.isEmpty()) {
            return
        }
        tiling.layout(this, bounds)
        for (i in children.size - 1 downTo 0) {
            children[i].layout(tiling)
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

    private fun depth(): Int {
        val p = this.parent
        return if (p == null) 0 else p.depth() + 1
    }

    private fun layout(tiling: MapLayout) {
        layout(tiling, mappable.bounds)
    }

}