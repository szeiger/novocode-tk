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

import java.io.IOException;
import java.util.Dictionary;

import com.novocode.tk.io.Latin1InputStreamReader;


/**
 * A class with a static utility method to parse an RFC822-style header
 * and create a Dictionary containing header names and their values.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class HeaderParser
{
  /* Dummy constructor */
  private HeaderParser() {}


  /**
   * Parses header lines and puts them into the supplied Dictionary.
   * Continuation lines and multiple entries are merged into single
   * headers. All header names are converted to lower case.
   *
   * @param in a Latin1InputStreamReader to read from.
   * @param header an existing Dictionary into which all headers are written.
   * @exception java.io.IOException if such an IOException is thrown by
   *            a method of the supplied reader.
   */

  public static void parseHeader(Latin1InputStreamReader in,
				 Dictionary header) throws IOException
  {
    String oldLine = null, line;

    while(true)
    {
      line=in.readLine();
      if(line.length() == 0)
      {
	if(oldLine != null) putHeaderLine(header, oldLine);
	break;
      }
      char c = line.charAt(0);
      if(c==' ' || c=='\t') oldLine += line;
      else
      {
	if(oldLine != null) putHeaderLine(header, oldLine);
	oldLine = line;
      }
    }
  }


  private static void putHeaderLine(Dictionary d, String header)
  {
    int sepPos = header.indexOf(':');
    String key = header.substring(0,sepPos).trim().toLowerCase();
    String val = header.substring(sepPos+1).trim();
    String oldval = (String)d.get(key);

    if(oldval == null) d.put(key, val);
    else d.put(key, oldval+", "+val);
  }
}
