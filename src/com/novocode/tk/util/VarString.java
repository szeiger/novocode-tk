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

import java.util.Dictionary;
import java.io.Serializable;


/**
 * A string containing variable names which can be expanded.
 *
 * <p>A VarString can contain Unix-style $VARIABLE directives. It can be
 * expanded by replacing all variable names by the variables' string values
 * given in a Dictionary.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class VarString implements Serializable
{
  /**
   * Expands a given String object by creating a new String in which all
   * variable names are replaced by the variables' values. If no definition
   * for a variable is found, the variable directive is left unchanged.
   * The shortest possible match is taken. "$$" is expanded to "$".
   *
   * @param in a String containing $VARIABLE directives.
   * @param dict a Dictionary which maps String keys to Object elements.
   *             Object.toString() is called on the elements to get their
   *             String representation.
   * @return the expanded String if a String and Dictionary were given,
   *         the original String if the Dictionary was null, null if the
   *         original String was null.
   */

  public static final String expand(String in, Dictionary dict)
  {
    if(in == null) return null;
    if(dict == null) return in;

    int inlen = in.length();
    StringBuffer ex = new StringBuffer(inlen * 2);
    for(int i=0; i<inlen; i++)
    {
      char ch = in.charAt(i);
      boolean appended = false;
      if(ch == '$')
      {
	StringBuffer buf = new StringBuffer(80);
	for(int j=i+1; j<inlen; j++)
	{
	  Object v = dict.get(buf.append(in.charAt(j)).toString());
	  if(v != null) { ex.append(v.toString()); i=j; appended=true; break; }
	}
      }
      if(!appended) ex.append(ch);
    }
    return ex.toString();
  }


  private String str, expanded;
  private Dictionary dict;
  private boolean mutableDict;


  /**
   * Creates a new VarString from the given String.
   *
   * @param str a String.
   * @param dict a Dictionary with the variables' definitions.
   * @param mutableDict set this to true if the given Dictionary could
   *                    be modified while being used by the VarString.
   *                    If the Dictionary is immutable expanded strings
   *                    can and will be cached.
   */

  public VarString(String str, Dictionary dict, boolean mutableDict)
  {
    this.str = str;
    this.dict = dict;
    this.mutableDict = mutableDict;
  }


  /**
   * Creates a new VarString from the given String.
   * This is a shortcut for VarString(str, null, true).
   *
   * @param str a String.
   * @see #VarString(java.lang.String, java.util.Dictionary, boolean)
   */

  public VarString(String str) { this(str, null, true); }


  /**
   * Creates a new null VarString.
   * This is a shortcut for VarString(null, null, true).
   *
   * @see #VarString(java.lang.String, java.util.Dictionary, boolean)
   */

  public VarString() { this(null, null, true); }


  /**
   * Sets the String on which the VarString operates.
   * If an expanded String is cached it is discarded.
   *
   * @param str a String.
   */

  public final synchronized void setString(String str)
  {
    this.str = str;
    this.expanded = null;
  }


  /**
   * @return the String on which this VarString operates.
   */

  public final String getString() { return str; }


  /**
   * Sets the Dictionary which is used for expanding the VarString.
   * If an expanded String is cached it is discarded.
   *
   * @param dict a Dictionary which maps Strings to Objects.
   * @param mutableDict set this to true if the given Dictionary could
   *                    be modified while being used by the VarString.
   *                    If the Dictionary is immutable expanded strings
   *                    can and will be cached.
   * @see #getDict
   * @see #mutableDict
   */

  public final synchronized void setDict(Dictionary dict, boolean mutableDict)
  {
    this.dict = dict;
    this.expanded = null;
    this.mutableDict = mutableDict;
  }


  /**
   * @return the dictionary which is used for expanding the VarString
   *         or null if no Dictionary has been set.
   * @see #setDict
   */

  public final Dictionary getDict() { return dict; }


  /**
   * @return true if the dictionary has been set to being mutable,
   *         otherwise false.
   * @see #setDict
   */

  public final boolean mutableDict() { return mutableDict; }


  /**
   * @return the expanded String.
   * @see #expand()
   */

  public final synchronized String toString() { return expand(); }


  /**
   * @return the expanded String.
   * @see #expand(java.lang.String, java.util.Dictionary)
   */

  public final synchronized String expand()
  {
    if(mutableDict) return expand(str, dict);
    if(expanded == null) expanded = expand(str, dict);
    return expanded;
  }


  /**
   * Returns the expanded String, using the given Dictionary.
   * This method does not change the default Dictionary and the cached copy
   * of the expanded String using the default Dictionary.
   *
   * @param dict a Dictionary which maps Strings to Objects.
   * @return a String.
   * @see #expand(java.lang.String, java.util.Dictionary)
   */

  public final synchronized String expand(Dictionary dict)
  {
    return expand(str, dict);
  }
}
