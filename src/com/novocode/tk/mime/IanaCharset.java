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

import java.util.*;


/**
 * A collection of static utility methods to find Java character encoding
 * names for IANA charset names.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class IanaCharset
{
  private static final Dictionary ianaCharsets = new Hashtable();

  static
  {
    /* ISO-8859-X */
    ianaCharsets.put("iso_ir_100", "8859_1");
    ianaCharsets.put("iso_8859_1", "8859_1");
    ianaCharsets.put("latin1", "8859_1");
    ianaCharsets.put("l1", "8859_1");
    ianaCharsets.put("ibm819", "8859_1");
    ianaCharsets.put("cp819", "8859_1");
    ianaCharsets.put("819", "8859_1");
    ianaCharsets.put("us_ascii", "8859_1");
    ianaCharsets.put("ascii", "8859_1");
    ianaCharsets.put("iso_8859_2", "8859_2");
    ianaCharsets.put("iso_ir_101", "8859_2");
    ianaCharsets.put("iso_8859_2", "8859_2");
    ianaCharsets.put("latin2", "8859_2");
    ianaCharsets.put("l2", "8859_2");
    ianaCharsets.put("iso_8859_3", "8859_3");
    ianaCharsets.put("iso_ir_109", "8859_3");
    ianaCharsets.put("latin3", "8859_3");
    ianaCharsets.put("iso_8859_4", "8859_4");
    ianaCharsets.put("iso_ir_110", "8859_4");
    ianaCharsets.put("latin4", "8859_4");
    ianaCharsets.put("l4", "8859_4");
    ianaCharsets.put("iso_8859_6", "8859_6");
    ianaCharsets.put("iso_ir_127", "8859_6");
    ianaCharsets.put("ecma_114", "8859_6");
    ianaCharsets.put("asmo_708", "8859_6");
    ianaCharsets.put("arabic", "8859_6");
    ianaCharsets.put("iso_8859_7", "8859_7");
    ianaCharsets.put("iso_ir_126", "8859_7");
    ianaCharsets.put("elot_928", "8859_7");
    ianaCharsets.put("ecma_118", "8859_7");
    ianaCharsets.put("greek", "8859_7");
    ianaCharsets.put("greek8", "8859_7");
    ianaCharsets.put("iso_8859_8", "8859_8");
    ianaCharsets.put("iso_ir_138", "8859_8");
    ianaCharsets.put("hebrew", "8859_8");
    ianaCharsets.put("iso_8859_5", "8859_5");
    ianaCharsets.put("iso_ir_144", "8859_5");
    ianaCharsets.put("cyrillic", "8859_5");
    ianaCharsets.put("iso_8859_9", "8859_9");
    ianaCharsets.put("iso_ir_148", "8859_9");
    ianaCharsets.put("latin5", "8859_9");
    ianaCharsets.put("l5", "8859_9");

    /* IBM Codepages */
    ianaCharsets.put("ebcdic_cp_dk", "Cp277");
    ianaCharsets.put("ebcdic_cp_co", "Cp277");
    ianaCharsets.put("ebcdic_cp_fi", "Cp278");
    ianaCharsets.put("ebcdic_cp_se", "Cp278");
    ianaCharsets.put("cp_is", "Cp861");
    ianaCharsets.put("cp_gr", "Cp869");
    ianaCharsets.put("ebcdic_cp_roece", "Cp870");
    ianaCharsets.put("ebcdic_cp_yu", "Cp870");
    ianaCharsets.put("ebcdic_cp_is", "Cp871");
    ianaCharsets.put("ebcdic_cp_ar2", "Cp918");
    ianaCharsets.put("ebcdic_cp_us", "Cp037");
    ianaCharsets.put("ebcdic_cp_ca", "Cp037");
    ianaCharsets.put("ebcdic_cp_wt", "Cp037");
    ianaCharsets.put("ebcdic_cp_nl", "Cp037");
    ianaCharsets.put("ebcdic_cp_it", "Cp280");
    ianaCharsets.put("ebcdic_cp_es", "Cp284");
    ianaCharsets.put("ebcdic_cp_gb", "Cp285");
    ianaCharsets.put("ebcdic_cp_fr", "Cp297");
    ianaCharsets.put("ebcdic_cp_ar1", "Cp420");
    ianaCharsets.put("ebcdic_cp_he", "Cp424");
    ianaCharsets.put("ebcdic_cp_be", "Cp500");
    ianaCharsets.put("ebcdic_cp_ch", "Cp500");
    ianaCharsets.put("cp_ar", "Cp868");

    /* Other */
    ianaCharsets.put("gb_2312_80", "GB2312");
    ianaCharsets.put("iso_ir_58", "GB2312");
    ianaCharsets.put("chinese", "GB2312");
    ianaCharsets.put("ks_c_5601_1987", "KSC5601");
    ianaCharsets.put("iso_ir_149", "KSC5601");
    ianaCharsets.put("ks_c_5601_1989", "KSC5601");
    ianaCharsets.put("ksc_5601", "KSC5601");
    ianaCharsets.put("korean", "KSC5601");
    ianaCharsets.put("jis_C6226_1983", "JIS0208");
    ianaCharsets.put("iso_ir_87", "JIS0208");
    ianaCharsets.put("x0208", "JIS0208");
    ianaCharsets.put("jis_x0208_1983", "JIS0208");
    ianaCharsets.put("euc_kr", "EUC");
    ianaCharsets.put("ks_c_5861_1992", "EUC");
    ianaCharsets.put("koi8_r", "KOI8_R");

    /* Missing: MacArabic */
    /* Missing: MacCentralEurope */
    /* Missing: MacCroatian */
    /* Missing: MacGreek */
    /* Missing: MacCyrillic */
    /* Missing: MacDingbat */
    /* Missing: MacHebrew */
    /* Missing: MacIceland */
    /* Missing: MacRoman */
    /* Missing: MacRomania */
    /* Missing: MacSymbol */
    /* Missing: MacThai */
    /* Missing: MacTurkish */
    /* Missing: MacUkraine */
    /* Missing: SingleByte */
    /* Missing: Big5 */
    /* Missing: CNS11643 */
    /* Missing: EUCJIS */
    /* Missing: JIS */
    /* Missing: JISAutoDetect */
    /* Missing: SJIS */
    /* Missing: DBCS_ASCII */
    /* Missing: DBCS_EBCDIC */
    /* Missing: MS874 */
  }


  /**
   * Finds an encoding for the specified charset.
   *
   * @param cs an IANA charset.
   * @return a possible encoding name. The encoding is not guaranteed
   *         to exist. The method never returns null. If everything else
   *         fails, the IANA charset name with '-' replaced by '_' is
   *         returned.
   */

  public static String encodingForIanaCharset(String cs)
  {
    String lw = cs.toLowerCase().replace('-','_');
    int colidx = lw.indexOf(':');
    if(colidx != -1) lw = lw.substring(0,colidx);
    String enc = (String)ianaCharsets.get(lw);
    if(enc != null) return enc;
    else if(lw.indexOf("jis") != -1) return "JISAutoDetect";
    else if((lw.indexOf("unicode") != -1) || (lw.indexOf("iso_10646") != -1))
    {
      if(lw.indexOf("utf_7") != -1) return "UTF7";
      else if(lw.indexOf("utf_8") != -1) return "UTF8";
      else if(lw.indexOf("big") != -1) return "UnicodeBig";
      else if(lw.indexOf("little") != -1) return "UnicodeLittle";
      else return "Unicode";
    }
    else if(lw.startsWith("ibm")) return "Cp"+lw.substring(3);
    else if(lw.startsWith("cp")) return "Cp"+lw.substring(2);
    else
    {
      try
      {
	int i = Integer.parseInt(lw, 10);
	return "Cp"+i;
      }
      catch(Throwable e)
      {
	return cs.replace('-','_');
      }
    }
  }


  /**
   * Finds an encoding for the specified content type header value.
   *
   * The "charset" attribute is extracted from the content type to find
   * an encoding with <EM>encodingForIanaCharset()</EM>.
   *
   * @param ctyp a MIME content type which is a sub-type of "text"
   * @return a possible encoding name if the "charset" attribute was
   *         found; otherwise null.
   * @see #encodingForIanaCharset
   */

  public static String encodingForContentType(String ctyp)
  {
    //-- Is the attribute processing correct?
    if(ctyp != null)
    {
      int i;
      while((i = ctyp.indexOf(';')) != -1)
      {
	ctyp = ctyp.substring(i+1).trim();
	String lw = ctyp.toLowerCase();
	if(lw.startsWith("charset="))
	{
	  String cs = ctyp.substring(8).trim();
	  i = cs.indexOf(';');
	  if(i != -1) cs = cs.substring(0,i);
	  i = cs.indexOf(' ');
	  if(i != -1) cs = cs.substring(0,i);
	  return encodingForIanaCharset(cs);
	}
      }
    }
    return null;
  }


  // Dummy constructor
  private IanaCharset() {}
}
