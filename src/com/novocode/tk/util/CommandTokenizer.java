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

import java.util.Vector;


/**
 * A class with a static utility method to tokenize a command string with
 * shell-like parsing.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class CommandTokenizer
{
  /* Dummy constructor */
  private CommandTokenizer() {}


  /**
   * Extracts whitespace-separated fragments from a string.
   * Parts quoted with single or double quotes are not split. Quote
   * characters can be escaped with a backslash.
   *
   * <P><STRONG>Example:</STRONG>
   * <CODE>CommandTokenizer.tokenize("/bin/sh -c 'ls -l'")</CODE>
   * returns the three tokens <CODE>{ "/bin/sh", "-c", "ls -l" }</CODE>.
   *
   * @param in the string to be tokenized.
   * @return an array containing the string fragments, or null if <EM>in</EM>
   *         is null or of zero length.
   */

  public static String[] tokenize(String in)
  {
    if(in == null || in.length() == 0) return null;

    Vector v = new Vector();
    StringBuffer b = new StringBuffer();
    for(int i=0; i<in.length(); i++)
    {
      if(in.charAt(i)==' ')
      {
	if(b.length() != 0)
	{
	  v.addElement(b.toString());
	  b.setLength(0);
	}
      }
      else if(in.charAt(i)=='\'')
      {
	int next = in.indexOf('\'', i+1);
	if(next != -1)
	{
	  b.append(in.substring(i+1, next));
	  i = next+1;
	}
      }
      else if(in.charAt(i)=='\"')
      {
	int next = in.indexOf('\"', i+1);
	if(next != -1)
	{
	  b.append(in.substring(i+1, next));
	  i = next+1;
	}
      }
      else b.append(in.charAt(i));
    }
    if(b.length() != 0) v.addElement(b.toString());

    String[] result = new String[v.size()];
    for(int i=0; i<v.size(); i++) result[i] = (String)(v.elementAt(i));
    return result;
  }
}
