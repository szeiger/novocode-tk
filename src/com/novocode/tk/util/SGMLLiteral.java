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

import java.util.Hashtable;


/**
 * A class with static utility methods to encode and decode SGML literals
 * as used by HTML.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class SGMLLiteral
{
  private static Hashtable entities;


  /* Dummy constructor */
  private SGMLLiteral() {}


  /** Encode a String as an SGML literal.
   * The characters '\t' (tab), '\n' (newline), '\r' (return), ' ' (space),
   * '&quot;' (double quote), '&amp;' (ampersand), '&lt;' (less than) and
   * '&gt;' (greater than) are replaced by SGML Entities so that the encoded
   * String, when displayed as part of an SGML document, will look like the
   * original String.
   *
   * @param s a String to be encoded.
   * @return the encoded String.
   */

  public static String encode(String s)
  {
    int l = s.length();
    StringBuffer b = new StringBuffer(l+10);

    for(int i=0; i<l; i++)
    {
      char c = s.charAt(i);

      switch(c)
      {
        case '\t': b.append("&#9;"); break;
        case '\n': b.append("&#10;"); break;
        case '\r': b.append("&#13;"); break;
        case ' ' : b.append("&#32;"); break;
        case '\"': b.append("&#34;"); break;
        case '&' : b.append("&#38;"); break;
        case '<' : b.append("&lt;"); break;
        case '>' : b.append("&gt;"); break;
        default  : b.append(c);
      }
    }

    return b.toString();
  }


  /** Decode a string which contains encoded entities.
   * All numeric entites and ISO-8859-1 character
   * entities of HTML 4.0 are supported.
   *
   * @param s a String to be decoded.
   * @return the decoded String.
   */

  public static String decode(String s)
  {
    int l = s.length();
    StringBuffer b = new StringBuffer(l);
    int idx = -1;
    for(int i=0; i<l; i++)
    {
      char c = s.charAt(i);
      if(idx == -1)
      {
	if(c == '&') idx = i;
	else b.append(c);
      }
      else if(c == ';')
      {
	char ent = decodeEntity(s.substring(idx+1,i));
	if(ent == -1) b.append(s.substring(idx,i+1));
	else b.append(ent);
	idx = -1;
      }
    }
    if(idx != -1) b.append(s.substring(idx,l));
    return b.toString();
  }


  /** Decode a single entity, without the leading "&amp;" and the
   * trailing ";" characters. The method returns -1 if the
   * entity is unknown. All numeric entites and ISO-8859-1 character
   * entities of HTML 4.0 are supported.
   *
   * @param s a String to be decoded.
   * @return the decoded String or -1.
   */

  public static char decodeEntity(String s)
  {
    if(s.length() == 0) return (char)-1;

    if(s.charAt(0) == '#')
    {
      try
      {
	return (char)Integer.parseInt(s.substring(1));
      }
      catch(NumberFormatException e) { return (char)-1; }
    }

    if(entities == null)
    {
      entities = new Hashtable();
      entities.put("quot"   , "\"");
      entities.put("amp"    , "&");
      entities.put("lt"     , "<");
      entities.put("gt"     , ">");
      entities.put("nbsp"   , String.valueOf((char)160));
      entities.put("iexcl"  , String.valueOf((char)161));
      entities.put("cent"   , String.valueOf((char)162));
      entities.put("pound"  , String.valueOf((char)163));
      entities.put("curren" , String.valueOf((char)164));
      entities.put("yen"    , String.valueOf((char)165));
      entities.put("brvbar" , String.valueOf((char)166));
      entities.put("sect"   , String.valueOf((char)167));
      entities.put("uml"    , String.valueOf((char)168));
      entities.put("copy"   , String.valueOf((char)169));
      entities.put("ordf"   , String.valueOf((char)170));
      entities.put("laquo"  , String.valueOf((char)171));
      entities.put("not"    , String.valueOf((char)172));
      entities.put("shy"    , String.valueOf((char)173));
      entities.put("reg"    , String.valueOf((char)174));
      entities.put("macr"   , String.valueOf((char)175));
      entities.put("deg"    , String.valueOf((char)176));
      entities.put("plusmn" , String.valueOf((char)177));
      entities.put("sup2"   , String.valueOf((char)178));
      entities.put("sup3"   , String.valueOf((char)179));
      entities.put("acute"  , String.valueOf((char)180));
      entities.put("micro"  , String.valueOf((char)181));
      entities.put("para"   , String.valueOf((char)182));
      entities.put("middot" , String.valueOf((char)183));
      entities.put("cedil"  , String.valueOf((char)184));
      entities.put("sup1"   , String.valueOf((char)185));
      entities.put("ordm"   , String.valueOf((char)186));
      entities.put("raquo"  , String.valueOf((char)187));
      entities.put("frac14" , String.valueOf((char)188));
      entities.put("frac12" , String.valueOf((char)189));
      entities.put("frac34" , String.valueOf((char)190));
      entities.put("iquest" , String.valueOf((char)191));
      entities.put("Agrave" , String.valueOf((char)192));
      entities.put("Aacute" , String.valueOf((char)193));
      entities.put("Acirc"  , String.valueOf((char)194));
      entities.put("Atilde" , String.valueOf((char)195));
      entities.put("Auml"   , String.valueOf((char)196));
      entities.put("Aring"  , String.valueOf((char)197));
      entities.put("AElig"  , String.valueOf((char)198));
      entities.put("Ccedil" , String.valueOf((char)199));
      entities.put("Egrave" , String.valueOf((char)200));
      entities.put("Eacute" , String.valueOf((char)201));
      entities.put("Ecirc"  , String.valueOf((char)202));
      entities.put("Euml"   , String.valueOf((char)203));
      entities.put("Igrave" , String.valueOf((char)204));
      entities.put("Iacute" , String.valueOf((char)205));
      entities.put("Icirc"  , String.valueOf((char)206));
      entities.put("Iuml"   , String.valueOf((char)207));
      entities.put("ETH"    , String.valueOf((char)208));
      entities.put("Ntilde" , String.valueOf((char)209));
      entities.put("Ograve" , String.valueOf((char)210));
      entities.put("Oacute" , String.valueOf((char)211));
      entities.put("Ocirc"  , String.valueOf((char)212));
      entities.put("Otilde" , String.valueOf((char)213));
      entities.put("Ouml"   , String.valueOf((char)214));
      entities.put("times"  , String.valueOf((char)215));
      entities.put("Oslash" , String.valueOf((char)216));
      entities.put("Ugrave" , String.valueOf((char)217));
      entities.put("Uacute" , String.valueOf((char)218));
      entities.put("Ucirc"  , String.valueOf((char)219));
      entities.put("Uuml"   , String.valueOf((char)220));
      entities.put("Yacute" , String.valueOf((char)221));
      entities.put("THORN"  , String.valueOf((char)222));
      entities.put("szlig"  , String.valueOf((char)223));
      entities.put("agrave" , String.valueOf((char)224));
      entities.put("aacute" , String.valueOf((char)225));
      entities.put("acirc"  , String.valueOf((char)226));
      entities.put("atilde" , String.valueOf((char)227));
      entities.put("auml"   , String.valueOf((char)228));
      entities.put("aring"  , String.valueOf((char)229));
      entities.put("aelig"  , String.valueOf((char)230));
      entities.put("ccedil" , String.valueOf((char)231));
      entities.put("egrave" , String.valueOf((char)232));
      entities.put("eacute" , String.valueOf((char)233));
      entities.put("ecirc"  , String.valueOf((char)234));
      entities.put("euml"   , String.valueOf((char)235));
      entities.put("igrave" , String.valueOf((char)236));
      entities.put("iacute" , String.valueOf((char)237));
      entities.put("icirc"  , String.valueOf((char)238));
      entities.put("iuml"   , String.valueOf((char)239));
      entities.put("eth"    , String.valueOf((char)240));
      entities.put("ntilde" , String.valueOf((char)241));
      entities.put("ograve" , String.valueOf((char)242));
      entities.put("oacute" , String.valueOf((char)243));
      entities.put("ocirc"  , String.valueOf((char)244));
      entities.put("otilde" , String.valueOf((char)245));
      entities.put("ouml"   , String.valueOf((char)246));
      entities.put("divide" , String.valueOf((char)247));
      entities.put("oslash" , String.valueOf((char)248));
      entities.put("ugrave" , String.valueOf((char)249));
      entities.put("uacute" , String.valueOf((char)250));
      entities.put("ucirc"  , String.valueOf((char)251));
      entities.put("uuml"   , String.valueOf((char)252));
      entities.put("yacute" , String.valueOf((char)253));
      entities.put("thorn"  , String.valueOf((char)254));
      entities.put("yuml"   , String.valueOf((char)255));
    }

    String dec = (String)entities.get(s);
    if(dec == null) return (char)-1;
    else return dec.charAt(0);
  }
}
