// Original work Copyright 2007 by L. Paul Chew.
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
 * Copyright (c) 2007 by L. Paul Chew.
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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An ArrayList implementation of Set. An ArraySet is good for small sets; it
 * has less overhead than a HashSet or a TreeSet.
 *
 * @author Paul Chew
 *
 *         Created December 2007. For use with Voronoi/Delaunay applet.
 *
 */
public class ArraySet<E> extends AbstractSet<E> implements Serializable
{

	private static final long serialVersionUID = -5570265660026562468L;

	private ArrayList<E> items; // Items of the set

	/**
	 * Create an empty set (default initial capacity is 3).
	 */
	public ArraySet()
	{
		this(3);
	}

	/**
	 * Create an empty set with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity
	 */
	public ArraySet(int initialCapacity)
	{
		items = new ArrayList<>(initialCapacity);
	}

	/**
	 * Create a set containing the items of the collection. Any duplicate items
	 * are discarded.
	 * 
	 * @param collection
	 *            the source for the items of the small set
	 */
	public ArraySet(Collection<? extends E> collection)
	{
		items = new ArrayList<>(collection.size());
		for (E item : collection) {
			if (!items.contains(item)) {
				items.add(item);
			}
		}
	}

	/**
	 * Get the item at the specified index.
	 * 
	 * @param index
	 *            where the item is located in the ListSet
	 * @return the item at the specified index
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of bounds
	 */
	public E get(int index) throws IndexOutOfBoundsException
	{
		return items.get(index);
	}

	/**
	 * True iff any member of the collection is also in the ArraySet.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @return true iff any member of collection appears in this ArraySet
	 */
	public boolean containsAny(Collection<?> collection)
	{
		for (Object item : collection) {
			if (this.contains(item)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean add(E item)
	{
		if (items.contains(item)) {
			return false;
		}
		return items.add(item);
	}

	@Override
	public Iterator<E> iterator()
	{
		return items.iterator();
	}

	@Override
	public int size()
	{
		return items.size();
	}

}
