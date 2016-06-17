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

public class TestPnt
{

	/**
	 * Main program (used for testing).
	 */
	public static void main(String[] args)
	{
		Pnt p = new Pnt(1, 2, 3);
		System.out.println("Pnt created: " + p);
		Pnt[] matrix1 = { new Pnt(1, 2), new Pnt(3, 4) };
		Pnt[] matrix2 = { new Pnt(7, 0, 5), new Pnt(2, 4, 6), new Pnt(3, 8, 1) };
		System.out.print("Results should be -2 and -288: ");
		System.out.println(Pnt.determinant(matrix1) + " "
				+ Pnt.determinant(matrix2));
		Pnt p1 = new Pnt(1, 1);
		Pnt p2 = new Pnt(-1, 1);
		System.out.println("Angle between " + p1 + " and " + p2 + ": "
				+ p1.angle(p2));
		System.out.println(p1 + " subtract " + p2 + ": " + p1.subtract(p2));
		Pnt v0 = new Pnt(0, 0), v1 = new Pnt(1, 1), v2 = new Pnt(2, 2);
		Pnt[] vs = { v0, new Pnt(0, 1), new Pnt(1, 0) };
		Pnt vp = new Pnt(.1, .1);
		System.out.println(vp + " isInside " + Pnt.toString(vs) + ": "
				+ vp.isInside(vs));
		System.out.println(v1 + " isInside " + Pnt.toString(vs) + ": "
				+ v1.isInside(vs));
		System.out.println(vp + " vsCircumcircle " + Pnt.toString(vs) + ": "
				+ vp.vsCircumcircle(vs));
		System.out.println(v1 + " vsCircumcircle " + Pnt.toString(vs) + ": "
				+ v1.vsCircumcircle(vs));
		System.out.println(v2 + " vsCircumcircle " + Pnt.toString(vs) + ": "
				+ v2.vsCircumcircle(vs));
		System.out.println("Circumcenter of " + Pnt.toString(vs) + " is "
				+ Pnt.circumcenter(vs));
	}

}
