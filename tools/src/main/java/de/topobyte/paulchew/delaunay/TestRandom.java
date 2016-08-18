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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.utilities.apache.commons.cli.OptionHelper;

/**
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 * 
 */
public class TestRandom
{

	final static Logger logger = LoggerFactory.getLogger(TestRandom.class);

	private static final String HELP_MESSAGE = TestRandom.class.getSimpleName()
			+ " [options]";

	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_NPOINTS = "npoints";
	private static final String OPTION_TRIANGLES = "triangles";

	public static void main(String args[]) throws IOException
	{
		int width = 800, height = 600;

		Options options = new Options();
		// @formatter:off
		OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "image output");
		OptionHelper.addL(options, OPTION_NPOINTS, true, false, "integer", "number of points to insert (default: 100)");
		OptionHelper.addL(options, OPTION_TRIANGLES, false, false, "specify this flag to draw the Delaunay triangulation on top of the Voronoi cells");
		// @formatter:on

		CommandLine line = null;
		try {
			line = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			System.out.println("unable to parse command line: "
					+ e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.setOptionComparator(null);
			formatter.printHelp(HELP_MESSAGE, options);
			System.exit(1);
		}
		if (line == null) {
			return;
		}

		String argOutput = line.getOptionValue(OPTION_OUTPUT);
		int npoints = 100;
		if (line.hasOption(OPTION_NPOINTS)) {
			String optionNPoints = line.getOptionValue(OPTION_NPOINTS);
			try {
				npoints = Integer.parseInt(optionNPoints);
			} catch (NumberFormatException e) {
				logger.error("unable to parse step value: " + e.getMessage());
			}
		}

		boolean drawTriangles = line.hasOption(OPTION_TRIANGLES);

		Triangulation<Integer> triangulation = createRandom(width, height,
				npoints);

		DelaunayDrawing.draw(Paths.get(argOutput), triangulation, width,
				height, drawTriangles);
	}

	private static Triangulation<Integer> createRandom(int width, int height,
			int n)
	{
		int initialSize = 10000;
		Triangle initialTriangle = new Triangle(new Pnt(-initialSize,
				-initialSize), new Pnt(initialSize, -initialSize), new Pnt(0,
				initialSize));
		Triangulation<Integer> t = new Triangulation<>(initialTriangle);
		Random random = new Random();
		for (int i = 0; i < n; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			t.delaunayPlace(new Pnt(x, y), i);
		}
		return t;
	}

}
