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


/**
 * This class contains a static utility method for decoding a String
 * encoded in the application/x-www-form-urlencoded MIME format.
 * This class is the counterpart to java.net.URLEncoder.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see java.net.URLEncoder
 */

public final class URLDecoder
{
  /* Dummy constructor */
  private URLDecoder() {}


  /**
   * Decode a String encoded in x-www-form-urlencoded format.
   *
   * @param s an encoded String.
   * @return the decoded String.
   */

  public static String decode(String s)
  {
    int l = s.length();
    StringBuffer b = new StringBuffer(l);

    for(int i=0; i<l; i++)
    {
      char c = s.charAt(i);

      if(c=='+') b.append(' ');
      else if(c=='%')
      {
	b.append((char)(Character.digit(s.charAt(i+1),16)*16
			+Character.digit(s.charAt(i+2),16)));
	i+=2;
      }
      else b.append(c);
    }

    return b.toString();
  }
}
