/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is Novocode Toolkit.
 *
 * The Initial Developer of the Original Code is
 * Stefan Zeiger <szeiger@novocode.com>. All Rights Reserved.
 *
 * A copy of the License is included in the file LICENSE.
 */


package com.novocode.tk.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;


/**
 * An enumeration that filters another enumeration.
 *
 * <p>To use FilteredEnumeration you have to subclass it and provide an
 * implementation of the contains() method.
 *
 * <p><STRONG>Example:</STRONG> The following code could be used to get
 * an Enumeration of all keys in a hashtable which are of type String.
 *
 * <pre>
 *     Hashtable hash = ...;
 *     Enumeration e = new FilteredEnumeration(hash.keys()) {
 *       public boolean contains(Object o) { return o instanceof String; }
 *     };
 * </pre>
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public abstract class FilteredEnumeration implements Enumeration
{
  private Enumeration e;
  private Object nextElement;


  /**
   * Creates a filter for an enumeration.
   *
   * @param e the enumeration which should be filtered.
   */

  public FilteredEnumeration(Enumeration e) { this.e = e; findNext(); }


  /**
   * Tests if an object is contained in this enumeration.
   *
   * <P>This method is called with objects from the original enumeration. Only
   * if it returns true the object in question is also contained in the
   * filtered enumeration. You have to provide an implementation for this
   * method in subclasses of FilteredEnumeration.
   *
   * @param o an object from the original enumeration.
   * @return true if the object is contained in this enumeration.
   */

  protected abstract boolean contains(Object o);


  private void findNext()
  {
    while(e.hasMoreElements())
    {
      nextElement = e.nextElement();
      if(contains(nextElement)) return;
    }
    nextElement = null;
  }


  /**
   * @return true if this enumeration contains more elements; false otherwise.
   */

  public boolean hasMoreElements() { return nextElement != null; }


  /**
   * @return the next element of this enumeration. 
   * @exception java.util.NoSuchElementException If no more elements exist.
   */

  public Object nextElement()
  {
    if(nextElement == null)
    {
      e.nextElement(); // should throw NoSuchElementException
      throw new NoSuchElementException(); // in case it didn't
    }
    Object o = nextElement;
    findNext();
    return o;
  }
}
