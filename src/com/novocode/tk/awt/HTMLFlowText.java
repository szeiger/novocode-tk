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


package com.novocode.tk.awt;

import com.novocode.tk.util.SGMLLiteral;


/**
 * HTMLFlowText is an AWT Component for displaying multi-line HTML text
 * with automatic line breaks.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class HTMLFlowText extends FlowText
{
  public HTMLFlowText() { super(); }

  public HTMLFlowText(int width) { super(width); }

  public HTMLFlowText(String s)
  {
    super();
    setText(s);
  }

  public HTMLFlowText(String s, int width)
  {
    super(width);
    setText(s);
  }


  /**
   * Sets a text of type text/html. Note that FlowText is by no means an
   * HTML text display object. Only a minimum subset of HTML is supported,
   * mainly &lt;P&gt;, &lt;BR&gt;, &lt;HR&gt; and &lt;Hx&gt; captions.
   */

  public void setText(String s)
  {
    StringBuffer token = new StringBuffer(), tagb = null, entb = null;
    tokens.setSize(0);
    for(int i=0; i<s.length(); i++)
    {
      char c = s.charAt(i);
      if(c == '<') tagb = new StringBuffer();
      else if(c == '>')
      {
	maybeAddToken(token);
	String tag = tagb.toString().toLowerCase();
	if(tag.equals("p"))
	{
	  tokens.addElement(T_NEWLINE);
	  tokens.addElement(T_NEWLINE);
	}
	else if(tag.equals("dd")) token.append(": ");
	else if(tag.equals("br") || tag.equals("dt") || tag.equals("h1") ||
		tag.equals("h2") || tag.equals("h3") || tag.equals("h4") ||
		tag.equals("h5") || tag.equals("/h1") || tag.equals("/h2") ||
		tag.equals("/h3") || tag.equals("/h4") || tag.equals("/h5"))
	  tokens.addElement(T_NEWLINE);
	else if(tag.startsWith("hr")) tokens.addElement(T_HRULE);
	tagb = null;
      }
      else if(tagb != null) tagb.append(c);
      else if(c == ' ' || c == '\r' || c == '\n' || c == '\t')
	maybeAddToken(token);
      else if(c == '&') entb = new StringBuffer();
      else if(entb != null)
      {
	if(c == ';')
	{
	  char dec = SGMLLiteral.decodeEntity(entb.toString());
	  if(dec == 160) token.append(' ');
	  else if(dec != -1) token.append(dec);
	  else token.append('&').append(entb).append(';');
	  entb = null;
	}
	else entb.append(c);
      }
      else token.append(c);
    }
    maybeAddToken(token);
    layoutDirty = true;
    if(isVisible()) repaint();
  }
}
