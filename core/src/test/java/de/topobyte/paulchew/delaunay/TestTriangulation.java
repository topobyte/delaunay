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

public class TestTriangulation
{

	/**
	 * Main program; used for testing.
	 */
	public static void main(String[] args)
	{
		Triangle tri = new Triangle(new Pnt(-10, 10), new Pnt(10, 10), new Pnt(
				0, -10));
		System.out.println("Triangle created: " + tri);
		Triangulation<Void> dt = new Triangulation<>(tri);
		System.out.println("DelaunayTriangulation created: " + dt);
		dt.delaunayPlace(new Pnt(0, 0), null);
		dt.delaunayPlace(new Pnt(1, 0), null);
		dt.delaunayPlace(new Pnt(0, 1), null);
		System.out.println("After adding 3 points, we have a " + dt);
		Triangle.moreInfo = true;
		System.out.println("Triangles: " + dt.getGraph().getNodes());
	}

}
