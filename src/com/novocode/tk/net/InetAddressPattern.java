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

import java.util.Vector;
import java.net.InetAddress;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;


/**
 * A pattern against which InetAddress objects can be matched.
 * This class manages a list of host name and IP address patterns which
 * are included or excluded in a given order.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class InetAddressPattern implements Serializable
{
  private Vector entries;
  private boolean matchAll, matchNone;


  /**
   * Create a new InetAddressPattern object which includes all hosts.
   */

  public InetAddressPattern() { matchAll = true; }


  /**
   * Create a new InetAddressPattern object from the given String.
   * The String can contain an arbitrary number of lines with one pattern
   * per line. A pattern starts with a "+" or "-" symbol (with "+" being
   * the default if there is no such symbol) which indicates whether the
   * pattern defines hosts to be in- or excluded. The host after the
   * initial symbol can be given either by its name or IP address. Host names
   * can start with a single "*" to match all host names that end with the
   * given string. IP addresses consist of 4 dot-separated bytes in decimal
   * notation, each of which can optionally be a "*" to match all addresses
   * with the same non-"*" bytes. At the beginning all hosts are included.
   *
   * <p>Example: The following piece of code creates an InetAddressPattern
   * which matches all hosts in 1.2.*.* except 1.2.3.* and foo.bar.com:
   * <pre>
   * InetAddressPattern pattern =
   *   new InetAddressPattern("-*\n"+
   *                          "+1.2.*.*\n"+
   *                          "-1.2.3.*\n"+
   *                          "-foo.bar.com");
   * </pre>
   *
   * @param s a String containing pattern entries.
   */

  public InetAddressPattern(String s)
  {
    entries = new Vector();
    try
    {
      BufferedReader rd = new BufferedReader(new StringReader(s));
      String line;
      while((line = rd.readLine()) != null) add(line.trim());
    }
    catch(IOException ignored) {}
    if(entries.size() == 0)
    {
      matchAll = true;
      entries = null;
    }
    else
    {
      InetAddressPatternEntry e = 
	(InetAddressPatternEntry)entries.elementAt(entries.size()-1);
      if((!e.include) && e.host==null && e.ip[0]==0xFF && e.ip[1]==0xFF &&
	 e.ip[2]==0xFF && e.ip[3]==0xFF)
      {
	matchNone = true;
	entries = null;
      }
    }
  }


  /**
   * Return the number of entries ("lines").
   *
   * @return the number of entries in the pattern.
   */

  public int size()
  {
    if(entries == null) return 0;
    return entries.size();
  }


  /**
   * Create a String representation of this pattern.
   *
   * @return a String containing all entries, separated by newline
   *         characters. The number of lines in the resulting String can be
   *         determined by calling size().
   * @see #size
   */

  public String toString()
  {
    if(matchAll) return "";
    if(matchNone) return "-*";

    StringBuffer b = new StringBuffer();
    for(int i=0; i<entries.size(); i++)
      ((InetAddressPatternEntry)entries.elementAt(i)).appendToStringBuffer(b);
    return b.toString();
  }


  private void add(String s)
  {
    if(s==null || s.length()==0) return;

    InetAddressPatternEntry e = new InetAddressPatternEntry();

    if(s.charAt(0)=='+')
    {
      s = s.substring(1);
      e.include = true;
    }
    else if(s.charAt(0)=='-') s = s.substring(1);
    else e.include = true;

    s = s.trim();

    if(s.length()==1 && s.charAt(0)=='*')
    {
      e.ip = new byte[4];
      e.ip[0] = e.ip[1] = e.ip[2] = e.ip[3] = (byte)0xFF;
    }
    else if(stringContainsLetters(s))
    {
      if(s.charAt(0)=='*')
      {
	e.host = s.substring(1);
	e.wildHost = true;
      }
      else e.host = s;
    }
    else
    {
      try
      {
	int dot1, dot2, dot3;
	if((dot1 = s.indexOf('.')) == -1) return;
	if((dot2 = s.indexOf('.', dot1+1)) == -1) return;
	if((dot3 = s.indexOf('.', dot2+1)) == -1) return;
	e.ip = new byte[4];
	String[] ip = new String[4];
	ip[0] = s.substring(0,dot1);
	ip[1] = s.substring(dot1+1,dot2);
	ip[2] = s.substring(dot2+1,dot3);
	ip[3] = s.substring(dot3+1);
	for(int i=0; i<4; i++)
	{
	  if(ip[i].length()==1 && ip[i].charAt(0)=='*') e.ip[i] = (byte)0xFF;
	  else e.ip[i] = (byte)(Integer.parseInt(ip[i]));
	}
      }
      catch(Exception makesMethodFail) { return; }
    }

    entries.addElement(e);
  }


  /**
   * Check if an InetAddress is matched by the pattern.
   *
   * @param ia an InetAddress.
   * @return true if the address is matched, otherwise false.
   */

  public boolean match(InetAddress ia)
  {
    if(matchAll) return true;
    if(matchNone) return false;

    byte[] ip = ia.getAddress();
    String host = ia.getHostName();
    boolean m = true;
    for(int i=0; i<entries.size(); i++)
      m = ((InetAddressPatternEntry)entries.elementAt(i)).match(ip, host, m);
    return m;
  }


  private static boolean stringContainsLetters(String s)
  {
    for(int i=0; i<s.length(); i++)
      if(Character.isLetter(s.charAt(i))) return true;
    return false;
  }


  private static final class InetAddressPatternEntry implements Serializable
  {
    String host;
    boolean wildHost;
    byte[] ip;
    boolean include;

    void appendToStringBuffer(StringBuffer b)
    {
      if(include) b.append('+');
      else b.append('-');
      if(host != null)
      {
	if(wildHost) b.append('*');
	b.append(host);
      }
      else if(ip[0]==(byte)0xFF && ip[1]==(byte)0xFF && ip[2]==(byte)0xFF &&
	      ip[3]==(byte)0xFF) b.append('*');
      else
      {
	if(ip[0]==(byte)0xFF) b.append('*'); else b.append((int)(ip[0]&0xFF));
	b.append('.');
	if(ip[1]==(byte)0xFF) b.append('*'); else b.append((int)(ip[1]&0xFF));
	b.append('.');
	if(ip[2]==(byte)0xFF) b.append('*'); else b.append((int)(ip[2]&0xFF));
	b.append('.');
	if(ip[3]==(byte)0xFF) b.append('*'); else b.append((int)(ip[3]&0xFF));
      }
      b.append('\n');
    }

    boolean match(byte[] a, String h, boolean m)
    {
      if(m == include) return m;

      if(host != null)
      {
	if(wildHost && h.endsWith(host)) return include;
	if(h.equals(host)) return include;
	return m;
      }
      else
      {
	for(int i=0; i<4; i++) if(ip[i]!=(byte)0xFF && ip[i]!=a[i]) return m;
	return include;
      }
    }
  }
}
