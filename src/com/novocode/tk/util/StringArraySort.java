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


/**
 * A collection of static utility methods to sort a String array by using
 * the compareTo() method of class String.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * This class will be replaced.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see java.lang.String#compareTo
 */

public final class StringArraySort
{
  /**
   * Sorts a String array in place with the Bubble Sort algorithm.
   *
   * @param a a String array to be sorted. The array is modified.
   * @return the same String array that was passed in.
   */

  public static String[] inPlaceBubble(String[] a)
  {
    for(int i=0; i<a.length; i++)
      for(int j=i+1; j<a.length; j++)
	if(a[i].compareTo(a[j])>0)
	{
	  String tmp = a[i];
	  a[i] = a[j];
	  a[j] = tmp;
	}

    return a;
  }


  /**
   * Sorts a String array in place with the Shell Sort algorithm.
   *
   * @param a a String array to be sorted. The array is modified.
   * @return the same String array that was passed in.
   */

  public static String[] inPlaceShell(String[] a)
  {
    int size = 1;

    while (size < a.length) size = 3 * size + 1;

    while((size /= 3) > 0)
      for(int i = 0; i < size && i + size < a.length; i++)
	for(int j = i + size; j < a.length; j += size)
	  for(int k = j; k >= size; k -= size)
	    if(a[k-size].compareTo(a[k])>0)
	    {
	      String tmp = a[k-size];
	      a[k-size] = a[k];
	      a[k] = tmp;
	    }
	    else break;

    return a;
  }
}
