// Copyright 2016 Sebastian Kuerten
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

package de.topobyte.paulchew.delaunay;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import de.topobyte.adt.graph.UndirectedGraph;
import de.topobyte.jts.utils.JtsHelper;
import de.topobyte.jts.utils.PolygonHelper;

/**
 * Several utility methods that are related to Voronoi Diagrams.
 * 
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class VoronoiUtil
{

	/**
	 * Create a mapping from site data objects to Voronoi cells.
	 * 
	 * @return the dual Voronoi cells.
	 */
	public static <T> Map<T, Geometry> getVoronoiCells(Triangulation<T> t)
	{
		Set<Pnt> done = new HashSet<>(t.getInitialTriangle());
		GeometryFactory factory = new GeometryFactory();
		Map<T, Geometry> map = new HashMap<>();
		for (Triangle triangle : t) {
			for (Pnt site : triangle) {
				if (done.contains(site)) {
					continue;
				}
				done.add(site);

				T thing = t.getData().get(site);
				List<Triangle> list = t.surroundingTriangles(site, triangle);
				TDoubleList xs = new TDoubleArrayList(list.size());
				TDoubleList ys = new TDoubleArrayList(list.size());
				Pnt[] vertices = new Pnt[list.size()];
				int i = 0;
				for (Triangle tri : list) {
					Pnt ccenter = tri.getCircumcenter();
					vertices[i++] = ccenter;
					xs.add(ccenter.coord(0));
					ys.add(ccenter.coord(1));
				}
				LinearRing ring = JtsHelper.toLinearRing(xs, ys, false);
				Polygon polygon = PolygonHelper.polygonFromLinearRing(ring,
						factory);
				map.put(thing, polygon);
			}
		}
		return map;
	}

	/**
	 * Create a neighbor graph of the inserted sites of a triangulation. Sites
	 * are considered neighbors if their Voronoi cells share a common edge.
	 * 
	 * @return a graph of sites.
	 */
	public static UndirectedGraph<Pnt> createSiteGraph(Triangulation<?> t)
	{
		UndirectedGraph<Pnt> graph = new UndirectedGraph<>();

		HashSet<Pnt> done = new HashSet<>(t.getInitialTriangle());
		for (Triangle triangle : t) {
			for (Pnt site : triangle) {
				if (done.contains(site)) {
					continue;
				}
				graph.addNode(site);
				done.add(site);
			}
		}

		done = new HashSet<>(t.getInitialTriangle());

		for (Triangle triangle : t) {
			for (Pnt site : triangle) {
				if (done.contains(site)) {
					continue;
				}
				done.add(site);
				if (!graph.containsNode(site)) {
					continue;
				}

				List<Triangle> list = t.surroundingTriangles(site, triangle);
				for (Triangle tri : list) {
					for (Pnt s : tri) {
						if (!graph.containsNode(s)) {
							continue;
						}
						if (!s.equals(site)) {
							graph.addEdge(site, s);
						}
					}
				}
			}
		}
		return graph;
	}

}
