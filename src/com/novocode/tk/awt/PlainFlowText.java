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


/**
 * PlainFlowText is an AWT Component for displaying multi-line plain text
 * with automatic line breaks.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class PlainFlowText extends FlowText
{
  public PlainFlowText() { super(); }

  public PlainFlowText(int width) { super(width); }

  public PlainFlowText(String s)
  {
    super();
    setText(s);
  }

  public PlainFlowText(String s, int width)
  {
    super(width);
    setText(s);
  }


  /**
   * Sets a text of type text/plain. All occurences of '\n' are converted
   * to hard line breaks. '\t' is converted to a horizontal line.
   */

  public void setText(String s)
  {
    StringBuffer token = new StringBuffer();
    tokens.setSize(0);
    for(int i=0; i<s.length(); i++)
    {
      char c = s.charAt(i);
      if(c == ' ') maybeAddToken(token);
      else if(c == '\n')
      {
	maybeAddToken(token);
	tokens.addElement(T_NEWLINE);
      }
      else if(c == '\t')
      {
	maybeAddToken(token);
	tokens.addElement(T_HRULE);
      }
      else token.append(c);
    }
    maybeAddToken(token);
    layoutDirty = true;
    if(isVisible()) repaint();
  }
}
