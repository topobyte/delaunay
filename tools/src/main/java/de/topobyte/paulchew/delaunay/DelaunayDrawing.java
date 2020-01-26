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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;

import de.topobyte.jgs.transform.IdentityCoordinateTransformer;
import de.topobyte.jts.utils.JtsHelper;
import de.topobyte.jts2awt.Jts2Awt;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 * 
 */
public class DelaunayDrawing
{

	/**
	 * Draw the triangulation to a PNG image file.
	 * 
	 * @param output
	 *            the file to create.
	 * @param triangulation
	 *            the triangulation to draw.
	 * @param width
	 *            the width of the image.
	 * @param height
	 *            the height of the image.
	 * @param drawTriangles
	 *            whether to draw the triangles of the Delaunay triangulation on
	 *            top of the Voronoi cells.
	 * @throws IOException
	 *             on IO failure when closing the image.
	 */
	public static void draw(Path output, Triangulation<Integer> triangulation,
			int width, int height, boolean drawTriangles) throws IOException
	{
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		IdentityCoordinateTransformer transform = new IdentityCoordinateTransformer();

		Color black = new Color(0xdd000000, true);
		Color red = new Color(0xddff0000, true);
		Color blue = new Color(0xdd0000ff, true);
		graphics.setStroke(new BasicStroke(1.0f));

		// draw voronoi cells
		Map<Integer, Geometry> voronoiCells = VoronoiUtil
				.getVoronoiCells(triangulation);

		graphics.setColor(red);
		for (Geometry g : voronoiCells.values()) {
			Shape area = Jts2Awt.toShape(g, transform);
			graphics.fill(area);
		}

		graphics.setColor(black);
		for (Geometry g : voronoiCells.values()) {
			Shape area = Jts2Awt.toShape(g, transform);
			graphics.draw(area);
		}

		// draw triangles if enabled
		if (drawTriangles) {
			Map<Integer, Triangle> triangles = triangulation.getTriangles();

			graphics.setColor(blue);
			for (int i : triangles.keySet()) {
				Triangle triangle = triangles.get(i);
				Geometry g = toGeometry(triangle);
				Shape area = Jts2Awt.toShape(g, transform);
				graphics.draw(area);
			}
		}

		ImageIO.write(image, "PNG", output.toFile());
	}

	private static Geometry toGeometry(Triangle triangle)
	{
		List<Double> xs = new ArrayList<>(3);
		List<Double> ys = new ArrayList<>(3);
		for (int i = 0; i < 3; i++) {
			Pnt pnt = triangle.get(i);
			double x = pnt.coord(0);
			double y = pnt.coord(1);
			xs.add(x);
			ys.add(y);
		}
		LinearRing ring = JtsHelper.toLinearRing(xs, ys, false);
		return ring.getFactory().createPolygon(ring, null);
	}

}
