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
 * A collection of static utility methods to manipulate Base64-encoded data.
 *
 * <p>This class uses single-line Base64 coding as required by RFC1945.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * Methods for encoding and decoding binary data should be added.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.util.Base16
 */

public final class Base64
{
  /* Dummy constructor */
  private Base64() {}


  /**
   * Encodes a Latin-1 string.
   * The code is aligned to 3 byte boundaries by appending '=' characters
   * at the end if necessary. The upper 8 bits are discarded.
   *
   * @param dec the string to be encoded.
   * @return The Base64 code for the string.
   * @see #encode(java.lang.String, boolean)
   * @see #decode
   */

  public static String encode(String dec)
  {
    return encode(dec, true);
  }


  /**
   * Encodes a Latin-1 string. The upper 8 bits are discarded.
   *
   * @param dec the string to be encoded.
   * @param align if set to true the code is aligned to 3 byte boundaries
   *              by appending '=' characters at the end if necessary.
   * @return the Base64 code for the string.
   * @see #encode(java.lang.String)
   * @see #decode
   */

  public static String encode(String dec, boolean align)
  {
    int l = dec.length();
    StringBuffer enc = new StringBuffer(((l*3)+1)/2);
    int state=0, group=0;

    for(int i=0; i<l; i++)
    {
      switch(state)
      {
        case 0:
	  group = dec.charAt(i);
	  break;

        case 1:
	  group = (group<<8) + dec.charAt(i);
	  break;

        default:
	  group = (group<<8) + dec.charAt(i);
	  enc.append(codes[(group>>18)&63]);
	  enc.append(codes[(group>>12)&63]);
	  enc.append(codes[(group>>6)&63]);
	  enc.append(codes[(group)&63]);
	  state = -1;
      }
      state++;
    }

    switch(state)
    {
      case 1:
	enc.append(codes[(group>>6)&63]);
	enc.append(codes[(group)&63]);
	if(align) enc.append("==");
	break;

      case 2:
	enc.append(codes[(group>>12)&63]);
	enc.append(codes[(group>>6)&63]);
	enc.append(codes[(group)&63]);
	if(align) enc.append("=");
    }

    return enc.toString();
  }


  /**
   * Decodes a Latin-1 string.
   *
   * @param enc the Base64 code of a Latin-1 string.
   * @return the decoded string.
   * @see #encode
   */

  public static String decode(String enc)
  {
    int l = enc.length();
    StringBuffer dec = new StringBuffer(l);
    int state=0, group=0;

    for(int i=0; i<l; i++)
    {
      byte b = (byte)(enc.charAt(i));
      int n=-1;

      if((b>=(byte)('A')) && (b<=(byte)('Z'))) n = b-(byte)('A');
      else if((b>=(byte)('a')) && (b<=(byte)('z'))) n = b-(byte)('a')+26;
      else if((b>=(byte)('1')) && (b<=(byte)('9'))) n = b-(byte)('1')+53;
      else if(b==(byte)('0')) n = 52;
      else if(b==(byte)('+')) n = 62;
      else if(b==(byte)('/')) n = 63;
      else if(b==(byte)('=')) break;
      else continue; // ignore unknown characters

      switch(state)
      {
        case 0:
	  group = n;
	  break;

        case 1:
        case 2:
	  group = (group<<6) + n;
	  break;

        default:
	  group = (group<<6) + n;
	  dec.append((char)((group>>16)&255));
	  dec.append((char)((group>>8)&255));
	  dec.append((char)(group&255));
	  state = -1;
      }
      state++;
    }

    switch(state)
    {
      case 3:
	dec.append((char)((group>>8)&255));
	dec.append((char)(group&255));
	break;
      case 2:
	dec.append((char)(group&255));
    }

    return dec.toString();
  }


  private static final char[] codes =
  {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
  };
}
