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


package com.novocode.tk.io;

import java.io.*;


/**
 * An HTMLStyleWriter is an extended PrintWriter that contains
 * methods for writing HTML code in selected styles.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Immature.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class HTMLStyleWriter extends PrintWriter
{
  public static final int S_TEXTONLY = 0;
  public static final int S_STANDARD = 1;
  public static final int S_FANCY    = 2;

  public int style;


  public HTMLStyleWriter(OutputStream os, int style)
  {
    super(os);
    this.style = style;
  }


  public HTMLStyleWriter(Writer wr, int style)
  {
    super(wr);
    this.style = style;
  }


  public void sendFormHeader(String context)
  {
    sendFormHeader(context, null);
  }


  public void sendFormHeader(String context, String encoding)
  {
    print("<FORM ENCTYPE=\"");
    if(encoding != null) print(encoding);
    else print("application/x-www-form-urlencoded");
    print("\" METHOD=POST>\r\n");
    if(context != null)
    {
      print("<INPUT TYPE=HIDDEN NAME=\"__context\" VALUE=\"");
      print(context);
      print("\">\r\n");
    }
  }


  public void sendFormFooter() { print("</FORM>\r\n"); }


  public void sendPageHeader(String what, String msg, String back,
			     String leftHeader, String rightHeader,
			     boolean sep)
  {
    //sendStatus(resultCode);
    //sendHeader(HD_CTYPE, "text/html");
    //if(!cache) sendHeader(HD_PRAGMA, "no-cache");
    //endHeader();

    print("<HTML>\r\n<HEAD>\r\n<TITLE>");
    if(leftHeader != null) print(leftHeader);
    print(what);
    print("</TITLE>\r\n</HEAD>\r\n");

    if(style == S_FANCY)
    {
      print("<BODY BGCOLOR=\"#B2B2B2\" TEXT=\"#000000\" "+
	    "ALINK=\"#FF0000\" VLINK=\"#551A8B\" "+
	    "LINK=\"#0000EE\">\r\n"+
	    "<TABLE CELLPADDING=3 CELLSPACING=0 WIDTH=\"100%\" "+
	    "BORDER=0><TR>\r\n"+
	    "<TD BGCOLOR=\"#000000\" ALIGN=LEFT>"+
	    "<FONT FACE=LUCIDA COLOR=\"#FE9304\"><B><I>");
      print(what);
      print("</I></B></FONT></TD>"+
	    "<TD BGCOLOR=\"#000000\" ALIGN=RIGHT>"+
	    "<FONT FACE=HELVETICA COLOR=\"#ffffff\"><B>");
      if(rightHeader != null) print(rightHeader);
      else print("NetForge");
      print("</B></FONT></TD></TR></TABLE><P>\r\n");
      if(back != null)
      {
	print("<FONT FACE=HELVETICA SIZE=-1>Go back to: <B>");
	print(back);
	print("</B></FONT>\r\n");
      }
      if(msg != null)
      {
	print("<P><TABLE CELLPADDING=3 CELLSPACING=0 WIDTH=\"100%\" "+
	      "BORDER=0><TR>\r\n"+
	      "<TD BGCOLOR=\"#000000\" ALIGN=LEFT>"+
	      "<FONT FACE=LUCIDA COLOR=\"#FE9304\"><B><I>");
	print(msg);
	print("</I></B></FONT></TD>"+
	      "<TD BGCOLOR=\"#000000\" ALIGN=RIGHT>"+
	      "<FONT FACE=HELVETICA COLOR=\"#FFFFFF\"><B>"+
	      "Error"+
	      "</B></FONT></TD></TR></TABLE><P>\r\n");
      }
      else if(back != null) print("<HR NOSHADE>\r\n");
      print("<P>\r\n");
    }
    else
    {
      if(style == S_TEXTONLY) print("<BODY>\r\n<H1>");
      else print("<BODY BGCOLOR=\"#B2B2B2\">\r\n<H1>");
      if(leftHeader != null) print(leftHeader);
      print(what);
      print("</H1>\r\n");
      if(back != null)
      {
	print("Back to: ");
	print(back);
      }
      if(msg != null)
      {
	print("\r\n<HR>\r\n<CENTER><B>Error: ");
	print(msg);
	print(".</B></CENTER>");
      }
      if(sep) print("\r\n<HR>\r\n<P>\r\n");
    }
  }


  public void sendPageFooter(String srvVer, Class cl, String host, int port)
  {
    if(style == S_FANCY)
    {
      print("<HR NOSHADE>\r\n<ADDRESS>\r\n"+
	    "<FONT FACE=HELVETICA SIZE=\"-1\"><I>"+
	    "Created by <B>NetForge/");
      print(srvVer);
      print("</B> (");
      print(cl.getName());
      print(") @ <B><A HREF=\"/\">");
      print(host);
      if(port != 80)
      {
	print(':');
	print(port);
      }
      print("</A></B></FONT></ADDRESS>\r\n</BODY>\r\n</HTML>\r\n");
    }
    else
    {
      print("<HR>\r\n<ADDRESS>Created by NetForge/");
      print(srvVer);
      print(" (");
      print(cl.getName());
      print(") @ <A HREF=\"/\">");
      print(host);
      print("</A></ADDRESS>\r\n</BODY>\r\n</HTML>\r\n");
    }
  }


  public void sendPageFooter(String appName, Class cl)
  {
    if(style == S_FANCY)
    {
      print("<HR NOSHADE>\r\n<ADDRESS>\r\n"+
	    "<FONT FACE=HELVETICA SIZE=\"-1\"><I>"+
	    "Created by <B>");
      print(appName);
      print("</B> (");
      print(cl.getName());
      print(")</FONT></ADDRESS>\r\n</BODY>\r\n</HTML>\r\n");
    }
    else
    {
      print("<HR>\r\n<ADDRESS>Created by ");
      print(appName);
      print(" (");
      print(cl.getName());
      print(")</ADDRESS>\r\n</BODY>\r\n</HTML>\r\n");
    }
  }


  public void sendItemName(String n) { sendItemName(n, null); }


  public void sendItemName(String n, String s)
  {
    if(style == S_FANCY)
    {
      print("<P><BR><TABLE CELLPADDING=3 CELLSPACING=0 "+
	    "WIDTH=\"100%\" BORDER=0><TR>\r\n"+
	    "<TD BGCOLOR=\"#9A9A9A\" ALIGN=LEFT>"+
	    "<FONT FACE=HELVETICA COLOR=\"#000000\"><B>");
      print(n);
      print("</B></FONT></TD>");
      if(s != null)
      {
	print("<TD BGCOLOR=\"#9A9A9A\" ALIGN=RIGHT>"+
	      "<FONT FACE=HELVETICA COLOR=\"#B2B2B2\"><B>");
	print(s);
	print("</B></FONT></TD>");
      }
      print("</TR></TABLE><P>\r\n");
    }
    else
    {
      print("<H3>");
      print(n);
      if(s != null)
      {
	print(" <I>(");
	print(s);
	print(")</I>");
      }
      print("</H3>\r\n");
    }
  }


  public void sendItemDescription(String d)
  {
    if(d==null) return;

    if(style == S_FANCY)
    {
      print("<FONT FACE=HELVETICA SIZE=-1><I>");
      print(d);
      print("</I><P></FONT>\r\n");
    }
    else
    {
      print("<I>");
      print(d);
      print("</I><BR>\r\n");
    }
  }


  public void sendPageDescription(String d)
  {
    if(d==null) return;

    if(style == S_FANCY)
    {
      print("<FONT FACE=HELVETICA SIZE=-1>");
      print(d);
      print("<P></FONT>\r\n");
    }
    else
    {
      print(d);
      print("<P>\r\n");
    }
  }


  public void startTextPage()
  {
    if(style == S_FANCY)
      print("<CENTER><TABLE BORDER=0 WIDTH=\"90%\" CELLPADDING=0 "+
	    "CELLSPACING=0><TR><TD><FONT FACE=HELVETICA SIZE=-1><BR>");
  }


  public void endTextPage()
  {
    if(style == S_FANCY)
      print("</FONT></TD></TR></TABLE></CENTER><P>\r\n");
    else print("<P>\r\n");
  }


  public void sendHR()
  {
    if(style == S_FANCY) print("<HR NOSHADE>\r\n");
    else print("<HR>\r\n");
  }


  public void startButtonArea()
  {
    print("<TABLE CELLPADDING=0 CELLSPACING=0 BORDER=0 WIDTH=\"100%\">"+
	  "<TR><TD ALIGN=LEFT>");
  }


  public void separateButtonArea() { print("</TD><TD ALIGN=RIGHT>"); }


  public void endButtonArea() { print("</TD></TR></TABLE>"); }


  public void sendButton(String name, String title)
  {
    if(style == S_FANCY)
    {
      print("<FONT FACE=LUCIDA SIZE=-1><I><INPUT TYPE=SUBMIT NAME=\"");
      if(name != null) print(name);
      print("__button\" VALUE=\"  ");
      //print(LiteralEncoder.encode(title));
      print(title);
      print("  \"></I></FONT>\r\n");
    }
    else
    {
      print("<INPUT TYPE=SUBMIT NAME=\"");
      if(name != null) print(name);
      print("__button\" VALUE=\"  ");
      //print(LiteralEncoder.encode(title));
      print(title);
      print("  \">\r\n");
    }
  }


  public void sendPageButton(String title) { sendButton(null, title); }


  public void sendResetButton()
  {
    if(style == S_FANCY)
      print("<FONT FACE=LUCIDA SIZE=-1><I><INPUT TYPE=RESET VALUE=\"  Reset "+
	    "form  \"></I></FONT>\r\n");
    else print("<INPUT TYPE=RESET VALUE=\"  Reset form  \">\r\n");
  }


  public void startList()
  {
    if(style == S_FANCY) print("<UL><FONT FACE=HELVETICA SIZE=-1>\r\n");
    else print("<UL>\r\n");
  }

  public void endList()
  {
    if(style == S_FANCY) print("</FONT></UL>\r\n");
    else print("</UL>\r\n");
  }

  public void sendListItem(String s) { sendListItem(s, null); }

  public void sendListItem(String s, String d)
  {
    print("<P><LI>");
    print(s);
    if(d != null)
    {
      print("<BR>");
      print(d);
    }
    print("\r\n");
  }
}
