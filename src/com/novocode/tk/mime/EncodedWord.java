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


package com.novocode.tk.mime;

import com.novocode.tk.util.Base64;


/**
 * A collection of static utility methods to manipulate Encoded Words
 * as defined in RFC1522 (MIME Part Two).
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively Mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class EncodedWord
{
  // Dummy Constructor
  private EncodedWord() {}


  /**
   * Decodes an encoded word. Supports B and Q encoding and the character
   * sets US-ASCII and ISO-8859-1.
   *
   * @param enc the encoded word.
   * @return the Unicode string representation of the encoded word or the
   *         original string if it could not be decoded.
   */

  public static String decodeWord(String enc)
  {
    int l = enc.length();
    if(l < 8
       || enc.charAt(0) != '='
       || enc.charAt(1) != '?'
       || enc.charAt(l-1) != '='
       || enc.charAt(l-2) != '?')
      return enc;

    int sep1 = enc.indexOf('?',2);
    if(sep1 == l-2) return enc;
    int sep2 = enc.indexOf('?',sep1+1);
    if(sep2 == l-2) return enc;

    String charset = enc.substring(2,sep1).toUpperCase();
    String coding = enc.substring(sep1+1,sep2).toUpperCase();
    String data = enc.substring(sep2+1,l-2);

    if(charset.equals("US-ASCII") || charset.equals("ISO-8859-1"))
    {
      if(coding.equals("B")) return Base64.decode(data);
      else if(coding.equals("Q")) return qDecode(data);
    }

    return enc;
  }


  private static String qDecode(String s)
  {
    int l = s.length();
    StringBuffer b = new StringBuffer(l);

    for(int i=0; i<l; i++)
    {
      char c = s.charAt(i);

      if(c=='_') b.append(' ');
      else if(c=='=')
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
