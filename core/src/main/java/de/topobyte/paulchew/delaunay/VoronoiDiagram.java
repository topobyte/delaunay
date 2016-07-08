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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import de.topobyte.jts.utils.JtsHelper;
import de.topobyte.jts.utils.PolygonHelper;

/**
 * This is a convenience wrapper for generating Voronoi Diagrams using the
 * Delaunay Triangulation. Further it allows a generic object to be associated
 * with any point used in the diagram. (in contrast to the original library,
 * which only enumerates the inserted points)
 * 
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 * 
 * @param <T>
 *            the type of objects associated with points.
 */
public class VoronoiDiagram<T> implements Serializable
{

	private static final long serialVersionUID = 4327366541235396721L;

	private Triangulation<T> t;

	/**
	 * Create an empty Voronoi Diagram. An initial triangle has to be specified
	 * that contains all sites that will be inserted into the diagram.
	 * 
	 * @param initialTriangle
	 *            the initial triangle.
	 */
	public VoronoiDiagram(Triangle initialTriangle)
	{
		t = new Triangulation<>(initialTriangle);
	}

	/**
	 * Create an empty Voronoi Diagram. An initial triangle has to be specified
	 * that contains all sites that will be inserted into the diagram.
	 * 
	 * @param x1
	 *            first coordinate of the initial triangle.
	 * @param y1
	 *            first coordinate of the initial triangle.
	 * @param x2
	 *            second coordinate of the initial triangle.
	 * @param y2
	 *            second coordinate of the initial triangle.
	 * @param x3
	 *            third coordinate of the initial triangle.
	 * @param y3
	 *            third coordinate of the initial triangle.
	 */
	public VoronoiDiagram(double x1, double y1, double x2, double y2,
			double x3, double y3)
	{
		Pnt p1 = new Pnt(x1, y1);
		Pnt p2 = new Pnt(x2, y2);
		Pnt p3 = new Pnt(x3, y3);
		Triangle initialTriangle = new Triangle(p1, p2, p3);
		t = new Triangulation<>(initialTriangle);
	}

	/**
	 * Add a point to the diagram at position <code>x, y</code> and associate
	 * <code>thing</code> with this point.
	 * 
	 * @param thing
	 *            the object to associate.
	 * @param x
	 *            the ordinate.
	 * @param y
	 *            the coordinate.
	 */
	public void put(T thing, double x, double y)
	{
		t.delaunayPlace(new Pnt(x, y), thing);
	}

	/**
	 * Retrieve a mapping from inserted objects to the polygons of the Voronoi
	 * Diagram.
	 * 
	 * @return the map from inserted objects to their polygons.
	 */
	public Map<T, Geometry> getPolygons()
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
				List<Double> xs = new ArrayList<>(list.size());
				List<Double> ys = new ArrayList<>(list.size());
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

}
