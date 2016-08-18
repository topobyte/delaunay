// Original work Copyright 2005, 2007 by L. Paul Chew.
// Modified work Copyright 2016 Sebastian Kuerten
//
// This file is part of delaunay.
//
// delaunay is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// delaunay is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with delaunay. If not, see <http://www.gnu.org/licenses/>.
//
// This is the header contained in the original file:

/*
 * Copyright (c) 2005, 2007 by L. Paul Chew.
 *
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package de.topobyte.paulchew.delaunay;

import gnu.trove.procedure.TObjectProcedure;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import de.topobyte.adt.graph.Graph;
import de.topobyte.adt.graph.UndirectedGraph;
import de.topobyte.jsi.GenericRTree;

/**
 * A 2D Delaunay Triangulation (DT) with incremental site insertion.
 *
 * This is not the fastest way to build a DT, but it's a reasonable way to build
 * a DT incrementally and it makes a nice interactive display. There are several
 * O(n log n) methods, but they require that the sites are all known initially.
 *
 * A Triangulation is a Set of Triangles. A Triangulation is unmodifiable as a
 * Set; the only way to change it is to add sites (via delaunayPlace).
 *
 * @author Paul Chew
 *
 *         Created July 2005. Derived from an earlier, messier version.
 *
 *         Modified November 2007. Rewrote to use AbstractSet as parent class
 *         and to use the Graph class internally. Tried to make the DT algorithm
 *         clearer by explicitly creating a cavity. Added code needed to find a
 *         Voronoi cell.
 *
 */
public class Triangulation<T> extends AbstractSet<Triangle> implements
		Serializable
{

	private static final long serialVersionUID = -3437333122694986680L;

	Map<Integer, Triangle> triangles = new HashMap<>();
	GenericRTree<Triangle> spidx = new GenericRTree<>();

	private Map<Pnt, T> pointToData;
	private Triangle initialTriangle;

	private Triangle mostRecent = null; // Most recently "active" triangle
	private UndirectedGraph<Triangle> triGraph; // Holds triangles for
												// navigation

	/**
	 * All sites must fall within the initial triangle.
	 * 
	 * @param triangle
	 *            the initial triangle
	 */
	public Triangulation(Triangle triangle)
	{
		initialTriangle = triangle;
		triGraph = new UndirectedGraph<>();
		pointToData = new HashMap<>();
		triGraph.addNode(triangle);
		mostRecent = triangle;

		spidx.add(DelaunayUtil.triangleBox(triangle), triangle);
		triangles.put(triangle.hashCode(), triangle);
	}

	/**
	 * @return the triangle this triangulation has been began with.
	 */
	public Triangle getInitialTriangle()
	{
		return initialTriangle;
	}

	/* The following two methods are required by AbstractSet */

	@Override
	public Iterator<Triangle> iterator()
	{
		return triGraph.getNodes().iterator();
	}

	@Override
	public int size()
	{
		return triGraph.getNodes().size();
	}

	public Graph<Triangle> getGraph()
	{
		return triGraph;
	}

	@Override
	public String toString()
	{
		return "Triangulation with " + size() + " triangles";
	}

	/**
	 * True iff triangle is a member of this triangulation. This method isn't
	 * required by AbstractSet, but it improves efficiency.
	 * 
	 * @param triangle
	 *            the object to check for membership
	 */
	@Override
	public boolean contains(Object triangle)
	{
		return triGraph.getNodes().contains(triangle);
	}

	/**
	 * Report neighbor opposite the given vertex of triangle.
	 * 
	 * @param site
	 *            a vertex of triangle
	 * @param triangle
	 *            we want the neighbor of this triangle
	 * @return the neighbor opposite site in triangle; null if none
	 * @throws IllegalArgumentException
	 *             if site is not in this triangle
	 */
	public Triangle neighborOpposite(Pnt site, Triangle triangle)
	{
		if (!triangle.contains(site)) {
			throw new IllegalArgumentException("Bad vertex; not in triangle");
		}
		for (Triangle neighbor : triGraph.getEdgesOut(triangle)) {
			if (!neighbor.contains(site)) {
				return neighbor;
			}
		}
		return null;
	}

	/**
	 * Return the set of triangles adjacent to triangle.
	 * 
	 * @param triangle
	 *            the triangle to check
	 * @return the neighbors of triangle
	 */
	public Set<Triangle> neighbors(Triangle triangle)
	{
		return triGraph.getEdgesOut(triangle);
	}

	/**
	 * Report triangles surrounding site in order (cw or ccw).
	 * 
	 * @param site
	 *            we want the surrounding triangles for this site
	 * @param triangle
	 *            a "starting" triangle that has site as a vertex
	 * @return all triangles surrounding site in order (cw or ccw)
	 * @throws IllegalArgumentException
	 *             if site is not in triangle
	 */
	public List<Triangle> surroundingTriangles(Pnt site, Triangle triangle)
	{
		if (!triangle.contains(site)) {
			throw new IllegalArgumentException("Site not in triangle");
		}
		List<Triangle> list = new ArrayList<>();
		Triangle start = triangle;
		Triangle current = triangle;
		Pnt guide = triangle.getVertexButNot(site); // Affects cw or ccw
		while (true) {
			list.add(current);
			Triangle previous = current;
			current = this.neighborOpposite(guide, current); // Next triangle
			guide = previous.getVertexButNot(site, guide); // Update guide
			if (current == start) {
				break;
			}
		}
		return list;
	}

	/**
	 * Locate the triangle with point inside it or on its boundary.
	 * 
	 * @param point
	 *            the point to locate
	 * @return the triangle that holds point; null if no such triangle
	 */
	public Triangle locate(final Pnt point)
	{
		Triangle triangle = mostRecent;
		if (!this.contains(triangle)) {
			triangle = null;
		}

		final Set<Triangle> founds = new HashSet<>();
		spidx.intersects(DelaunayUtil.pntBox(point),
				new TObjectProcedure<Triangle>() {

					@Override
					public boolean execute(Triangle striangle)
					{
						// check for containment
						if (point.isOutside(striangle.toArray(new Pnt[0])) == null) {
							founds.add(striangle);
						}
						return true;
					}
				});
		if (founds.size() > 1) {
			System.out.println(String.format("size is: %d of point %s",
					founds.size(), point.stringRepresentation()));
			for (Triangle t : founds) {
				System.out.println(t.stringRepresentation());
			}
		}

		if (founds.size() == 1) {
			return founds.iterator().next();
		}
		for (Triangle t : founds) {
			Set<Triangle> cavity = getCavity(point, t);
			if (cavity.size() != 0) {
				return t;
			}
		}

		return null;
	}

	/**
	 * Place a new site into the DT. Nothing happens if the site matches an
	 * existing DT vertex.
	 * 
	 * @param site
	 *            the new Pnt
	 * @param data
	 *            the data to associate with this site.
	 * @throws IllegalArgumentException
	 *             if site does not lie in any triangle
	 */
	public void delaunayPlace(Pnt site, T data)
	{
		// Uses straightforward scheme rather than best asymptotic time

		// Locate containing triangle
		Triangle triangle = locate(site);
		// Give up if no containing triangle or if site is already in DT
		if (triangle == null) {
			System.out.println(site.stringRepresentation());
			throw new IllegalArgumentException("No containing triangle");
		}
		if (triangle.contains(site)) {
			return;
		}

		// Determine the cavity and update the triangulation
		Set<Triangle> cavity = getCavity(site, triangle);
		mostRecent = update(site, cavity);

		this.pointToData.put(site, data);
	}

	/**
	 * Determine the cavity caused by site.
	 * 
	 * @param site
	 *            the site causing the cavity
	 * @param triangle
	 *            the triangle containing site
	 * @return set of all triangles that have site in their circumcircle
	 */
	private Set<Triangle> getCavity(Pnt site, Triangle triangle)
	{
		Set<Triangle> encroached = new HashSet<>();
		Queue<Triangle> toBeChecked = new LinkedList<>();
		Set<Triangle> marked = new HashSet<>();
		toBeChecked.add(triangle);
		marked.add(triangle);
		while (!toBeChecked.isEmpty()) {
			Triangle current = toBeChecked.remove();
			if (site.vsCircumcircle(current.toArray(new Pnt[0])) == 1) {
				continue; // Site outside triangle => triangle not in cavity
			}
			encroached.add(current);
			// Check the neighbors
			for (Triangle neighbor : triGraph.getEdgesOut(current)) {
				if (marked.contains(neighbor)) {
					continue;
				}
				marked.add(neighbor);
				toBeChecked.add(neighbor);
			}
		}
		return encroached;
	}

	/**
	 * Update the triangulation by removing the cavity triangles and then
	 * filling the cavity with new triangles.
	 * 
	 * @param site
	 *            the site that created the cavity
	 * @param cavity
	 *            the triangles with site in their circumcircle
	 * @return one of the new triangles
	 */
	private Triangle update(Pnt site, Set<Triangle> cavity)
	{
		Set<Set<Pnt>> boundary = new HashSet<>();
		Set<Triangle> theTriangles = new HashSet<>();

		// Find boundary facets and adjacent triangles
		// System.out.println("cavity size: " + cavity.size());
		for (Triangle triangle : cavity) {
			theTriangles.addAll(neighbors(triangle));
			for (Pnt vertex : triangle) {
				Set<Pnt> facet = triangle.facetOpposite(vertex);
				if (boundary.contains(facet)) {
					boundary.remove(facet);
				} else {
					boundary.add(facet);
				}
			}
		}
		theTriangles.removeAll(cavity); // Adj triangles only

		// Remove the cavity triangles from the triangulation
		for (Triangle triangle : cavity) {
			triGraph.removeNode(triangle);
			triangles.remove(triangle.hashCode());
			spidx.delete(DelaunayUtil.triangleBox(triangle), triangle);
		}

		// Build each new triangle and add it to the triangulation
		Set<Triangle> newTriangles = new HashSet<>();
		if (boundary.size() == 0) {
			System.out.println("no boundary facets found");
		}
		for (Set<Pnt> vertices : boundary) {
			vertices.add(site);
			Triangle tri = new Triangle(vertices);
			triGraph.addNode(tri);
			triangles.put(tri.hashCode(), tri);
			spidx.add(DelaunayUtil.triangleBox(tri), tri);
			newTriangles.add(tri);
		}

		// Update the graph links for each new triangle
		theTriangles.addAll(newTriangles); // Adj triangle + new triangles
		for (Triangle triangle : newTriangles) {
			for (Triangle other : theTriangles) {
				if (triangle.isNeighbor(other)) {
					triGraph.addEdge(triangle, other);
				}
			}
		}

		// Return one of the new triangles
		return newTriangles.iterator().next();
	}

	/**
	 * @return the set of points and their associated objects.
	 */
	public Map<Pnt, T> getData()
	{
		return pointToData;
	}

	/**
	 * Get the triangles
	 * 
	 * @return the triangles of the triangulation
	 */
	public Map<Integer, Triangle> getTriangles()
	{
		return triangles;
	}

}