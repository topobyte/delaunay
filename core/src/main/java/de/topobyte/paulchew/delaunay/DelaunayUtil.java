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

import com.infomatiq.jsi.Rectangle;

/**
 * Several methods that are provided for for speeding up the generation of a
 * delaunay triangulation.
 * 
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class DelaunayUtil
{

	/**
	 * Create a Rectangle that contains exactly this single point.
	 * 
	 * @param pnt
	 *            the point to construct a rectangle for.
	 * @return the constructed rectangle.
	 */
	public static Rectangle pntBox(Pnt pnt)
	{
		return new Rectangle((float) pnt.coord(0), (float) pnt.coord(1),
				(float) pnt.coord(0), (float) pnt.coord(1));
	}

	/**
	 * Create a Rectangle that is a bounding box for the polygon specified by
	 * <code>vertices</code>.
	 * 
	 * @param vertices
	 *            the representation of a polygon.
	 * @return the bounding box of <code>vertices</code>.
	 */
	public static Rectangle polygonBox(Pnt[] vertices)
	{
		double x0 = vertices[0].coord(0);
		double y0 = vertices[0].coord(1);
		double x1 = vertices[0].coord(0);
		double y1 = vertices[0].coord(1);
		for (int i = 1; i < vertices.length; i++) {
			double x = vertices[i].coord(0);
			double y = vertices[i].coord(1);
			if (x < x0)
				x0 = x;
			if (x > x1)
				x1 = x;
			if (y < y0)
				y0 = y;
			if (y > y1)
				y1 = y;
		}
		return new Rectangle((float) x0, (float) y0, (float) x1, (float) y1);
	}

	/**
	 * Create a Rectangle that is the bounding box of <code>triangle</code>.
	 * 
	 * @param triangle
	 *            the triangle to compute the bounding box for.
	 * @return the computed bounding box.
	 */
	public static Rectangle triangleBox(Triangle triangle)
	{
		double x0;
		double x1;
		double y0;
		double y1;
		x0 = triangle.get(0).coord(0);
		y0 = triangle.get(0).coord(1);
		x1 = x0;
		y1 = y0;
		for (int i = 1; i <= 2; i++) {
			double x = triangle.get(i).coord(0);
			double y = triangle.get(i).coord(1);
			if (x < x0)
				x0 = x;
			if (x > x1)
				x1 = x;
			if (y < y0)
				y0 = y;
			if (y > y1)
				y1 = y;
		}
		return new Rectangle((float) x0, (float) y0, (float) x1, (float) y1);
	}

}
