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


package com.novocode.tk.net;

import java.util.*;
import java.io.*;


/**
 * An associative table for parameters of an HTTP request.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class FormData
{
  private Hashtable hash = new Hashtable();


  /**
   * Create an empty FormData object.
   */

  public FormData() {}


  /**
   * Create a new FormData object, reading the parameters of a POST request.
   *
   * @param in an InputStreams which returns the parameters in
   *           application/x-www-form-urlencoded coding.
   * @param clen the number of bytes which may be read from the stream.
   */

  public FormData(InputStream in, int clen) throws IOException
  {
    byte[] bytes = new byte[clen];
    in.read(bytes);
    init(new String(bytes, "8859_1"));
  }


  /**
   * Create a new FormData object from the parameters given in an HTTP URI.
   *
   * @param uri a String containing an HTTP URI or the parameter part of
   *            such a URI.
   */

  public FormData(String uri)
  {
    int sep = uri.indexOf('?');
    if(sep!=-1) uri = uri.substring(sep+1);

    init(uri);
  }


  private void init(String s)
  {
    int sep;
    while(true)
    {
      if((sep=s.indexOf('&'))==-1) sep=s.indexOf(';');
      if(sep==-1) { addEncoded(s.substring(0)); break; }
      addEncoded(s.substring(0,sep));
      s = s.substring(sep+1);
    }
  }


  private void addEncoded(String s)
  {
    int sep = s.indexOf('=');
    if(sep < 1) return;
    String k = URLDecoder.decode(s.substring(0,sep));
    String e = URLDecoder.decode(s.substring(sep+1));

    Object old = hash.put(k, e);
    if(old != null)
    {
      if(old instanceof String)
      {
	String[] a = new String[2];
	a[0] = (String)old;
	a[1] = e;
	hash.put(k, a);
      }
      else
      {
	String[] o = (String[])old;
	String[] a = new String[o.length+1];
	for(int i=0; i<o.length; i++) a[i] = o[i];
	a[o.length] = e;
	hash.put(k, a);
      }
    }
  }


  /**
   * Return the value of a parameter.
   *
   * @param key a String containing the name of a parameter. The name is
   *            case-sensitive.
   * @return null if the parameter was not found, a String containing the
   *         parameter's value if the parameter was defined once, or a
   *         String containing a space-separated concatenation of all values
   *         if the parameter was defined multiple times.
   * @see #getAll
   */

  public String get(String key)
  {
    Object o = hash.get(key);
    if(o == null) return null;
    if(o instanceof String) return (String)o;
    String[] a = (String[])o;
    StringBuffer b = new StringBuffer(a[0]);
    for(int i=1; i<a.length; i++) b.append(' ').append(a[i]);
    return b.toString();
  }


  /**
   * Return all values of a parameter.
   *
   * @param key a String containing the name of a parameter. The name is
   *            case-sensitive.
   * @return a String array containing all values for the parameter, or null
   *         if the parameter was not found.
   * @see #get
   */

  public String[] getAll(String key)
  {
    Object o = hash.get(key);
    if(o == null) return null;
    if(o instanceof String)
    {
      String[] a = new String[1];
      a[0] = (String)o;
      return a;
    }
    return (String[])o;
  }


  /**
   * @return an Enumeration of all parameters in this table
   */

  public Enumeration keys()
  {
    return hash.keys();
  }


  /**
   * @return a String representation
   */

  public String toString()
  {
    StringBuffer b = new StringBuffer(super.toString());
    b.append(": \n");
    Enumeration keys = keys();
    while(keys.hasMoreElements())
    {
      String key = (String)keys.nextElement();
      b.append(key).append(" = ").append(get(key)).append('\n');
    }
    return b.toString();
  }
}
