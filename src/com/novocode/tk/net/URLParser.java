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

import java.net.MalformedURLException;


/**
 * This class implements a parser for URLs that can be used more
 * universally than java.net.URL's parsing methods.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see java.net.URL
 */

public class URLParser
{
  private String protocol, authinfo, host, file, options, anchor;
  private int port = -1;


  /** Create a new URLParser object from a given external form of a URL.
   *
   * @param externalForm a String containing the external form of a URL,
   *        e.g. "http://user:pass@foo:8080/bar?opt=val#anchor"
   * @exception java.net.MalformedURLException if the external form could
   *            not be parsed.
   */

  public URLParser(String externalForm) throws MalformedURLException
  {
    // Get protocol
    {
      int slashIdx = externalForm.indexOf('/');
      int colonIdx = externalForm.indexOf(':');
      if((colonIdx != -1) && (colonIdx < slashIdx))
      {
	protocol = externalForm.substring(0, colonIdx);
	externalForm = externalForm.substring(colonIdx+1);
      }
    }

    // Get options and anchor
    {
      int hashIdx = externalForm.indexOf('#');
      if(hashIdx != -1)
      {
	anchor = externalForm.substring(hashIdx+1);
	externalForm = externalForm.substring(0, hashIdx);
      }
      int questIdx = externalForm.indexOf('?');
      if(questIdx != -1)
      {
	options = externalForm.substring(questIdx+1);
	externalForm = externalForm.substring(0, questIdx);
      }
    }

    // Get the rest
    if(externalForm.startsWith("//"))
    {
      externalForm = externalForm.substring(2);
      int slashIdx = externalForm.indexOf('/');
      int atIdx = externalForm.indexOf('@');
      if((atIdx != -1) && ((atIdx < slashIdx) || (slashIdx == -1)))
      {
	authinfo = externalForm.substring(0, atIdx);
	externalForm = externalForm.substring(atIdx+1);
      }
      if(slashIdx != -1)
      {
	slashIdx = externalForm.indexOf('/');
	file = externalForm.substring(slashIdx+1);
	externalForm = externalForm.substring(0, slashIdx);
      }
      int colonIdx = externalForm.indexOf(':');
      try
      {
	if(colonIdx == -1) host = externalForm;
	else if(colonIdx == 0)
	{
	  port = Integer.parseInt(externalForm);
	}
	else
	{
	  host = externalForm.substring(0, colonIdx);
	  port = Integer.parseInt(externalForm.substring(colonIdx+1));
	}
      }
      catch(NumberFormatException e)
      {
	throw new MalformedURLException("Illegal port number");
      }
    }
    else if(externalForm.length() > 1) file = externalForm.substring(1);
  }


  /**
   * @return the protocol part of the URL, e.g. "http" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor"
   */

  public String getProtocol() { return protocol; }


  /**
   * @return the authentication info part of the URL, e.g. "user:pass" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor"
   */

  public String getAuthInfo() { return authinfo; }


  /**
   * @return the host part of the URL, e.g. "foo" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor"
   */

  public String getHost() { return host; }


  /**
   * @return the file part of the URL, e.g. "bar" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor"
   */

  public String getFile() { return file; }


  /**
   * @return the options part of the URL, e.g. "opt=val" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor"
   */

  public String getOptions() { return options; }


  /**
   * @return the anchor part of the URL, e.g. "anchor" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor"
   */

  public String getAnchor() { return anchor; }


  /**
   * @return the port part of the URL, e.g. "8080" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor",
   *         or -1 if no port was given
   */

  public int getPort() { return port; }


  /**
   * @param the default port that is used for URLs without an
   *        explicit port
   *
   * @return the port part of the URL, e.g. "8080" in
   *         "http://user:pass@foo:8080/bar?opt=val#anchor",
   *         or the supplied default port if no port was given
   */

  public int getPort(int defaultPort)
  {
    if(port == -1) return defaultPort;
    else return port;
  }


  /**
   * @param the default port that is used for URLs without an
   *        explicit port
   *
   * @return the external form uf the URL, e.g.
   *         "http://user:pass@foo:8080/bar?opt=val#anchor". The
   *         port is omitted if it equals the supplied default port.
   */

  public String toExternalForm(int defPort)
  {
    StringBuffer b = new StringBuffer();
    if(protocol != null)
    {
      b.append(protocol).append(':');
      if(host != null)
      {
	b.append("//");
	if(authinfo != null) b.append(authinfo).append('@');
	b.append(host);
	if((port != -1) && (port != defPort)) b.append(':').append(port);
      }
      if(file != null) b.append('/').append(file);
      if(options != null) b.append('?').append(options);
      if(anchor != null) b.append('#').append(anchor);
    }
    return b.toString();
  }


  /**
   * @return the external form uf the URL, e.g.
   *         "http://user:pass@foo:8080/bar?opt=val#anchor".
   *         If the URLParser was created without a port, no port
   *         is included in the external form.
   */

  public String toExternalForm() { return toExternalForm(-1); }


  /** @return the external form of the URL as returned by toExternalForm().
   *
   * @see #toExternalForm
   */

  public String toString() { return toExternalForm(-1); }
}
