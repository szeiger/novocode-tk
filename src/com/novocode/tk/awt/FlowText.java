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

import java.util.*;
import java.awt.*;


/**
 * FlowText is the abstract superclass for AWT Components which display
 * multi-line text with automatic line breaks.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.awt.HTMLFlowText
 * @see com.novocode.tk.awt.PlainFlowText
 */

public abstract class FlowText extends Canvas
{
  private Vector lines = new Vector();
  private Dimension minDim = new Dimension(10,10), prefDim;
  private int prefWidth = 300, height, width = 300;
  private boolean forceSize, optWidth;
  private FontMetrics fm;

  protected static final Object T_NEWLINE = new Object();
  protected static final Object T_HRULE = new Object();
  protected Vector tokens = new Vector();
  protected boolean layoutDirty = true;


  /**
   * Creates a new FlowText with the default width of 300 pixels.
   */

  public FlowText() {}


  /**
   * Creates a new FlowText with the specified width.
   */

  public FlowText(int width)
  {
    this.width = width;
    prefWidth = width;
  }


  public Dimension getPreferredSize()
  {
    checkLayout();
    return prefDim;
  }


  public Dimension getMinimumSize()
  {
    if(forceSize) return getPreferredSize();
    else return minDim;
  }


  /**
   * If set to true, getMinimumSize() returns the preferred size,
   * otherwise (10,10).
   *
   * forceSize defaults to false.
   */

  public void setForceSize(boolean b) { forceSize = true; }


  /**
   * If set to true, the width is reduced so the text just fits into
   * the FlowText. Otherwise the specified width is used even if the
   * text is smaller.
   *
   * optimizeWidth defaults to false.
   */

  public void setOptimizeWidth(boolean b) { optWidth = true; }


  public void update(Graphics g) { paint(g); }


  public void paint(Graphics g)
  {
    checkSize();
    checkLayout();

    int h = fm.getHeight();
    int y = fm.getMaxAscent();
    int size = lines.size();

    Dimension d = getSize();
    g.setColor(getBackground());
    g.fillRect(0, 0, d.width, d.height);
    g.setColor(getForeground());

    for(int i=0; i<size; i++)
    {
      Object o = lines.elementAt(i);
      if(o instanceof String) g.drawString((String)o, 0, y);
      else if(o == T_HRULE)
      {
	int liney = y - fm.getMaxAscent()/2;
	g.drawLine(0, liney, width, liney);
      }
      y += h;
    }
  }


  /**
   * Sets the preferred width in pixels.
   */

  public void setPreferredWidth(int i)
  {
    prefWidth = i;
    layoutDirty = true;
  }


  /**
   * @return the preferred width.
   */

  public int getPreferredWidth() { return prefWidth; }



  /**
   * Sets a text. Subclasses have to implement this method.
   */

  public abstract void setText(String s);


  private void checkSize()
  {
    Dimension dim = getSize();
    if(width != dim.width || height != dim.height)
    {
      width = dim.width;
      height = dim.height;
      layoutDirty = true;
    }
  }


  private void checkLayout()
  {
    int maxw = 0;
    if(fm == null) fm = getFontMetrics(getFont());

    if(layoutDirty)
    {
      String line = null, testline;
      lines.setSize(0);
      int tokSize = tokens.size();
      for(int i=0; i<tokSize; i++)
      {
	Object token = tokens.elementAt(i);
	if(token == T_NEWLINE)
	{
	  if(line != null)
	  {
	    lines.addElement(line);
	    line = null;
	  }
	  else lines.addElement("");
	}
	else if(token == T_HRULE)
	{
	  if(line != null)
	  {
	    lines.addElement(line);
	    line = null;
	  }
	  lines.addElement(T_HRULE);
	}
	else if(token instanceof String)
	{
	  if(line == null) testline = (String)token;
	  else testline = line + ' ' + (String)token;
	  int strw = fm.stringWidth(testline);
	  if(strw > width)
	  {
	    lines.addElement(line);
	    line = (String)token;
	  }
	  else
	  {
	    line = testline;
	    if(strw > maxw) maxw = strw;
	  }
	}
      }
      if(line != null) lines.addElement(line);
      layoutDirty = false;

      height = fm.getHeight()*(lines.size()-1)
	+ fm.getMaxAscent() + fm.getMaxDescent();
      if(optWidth) prefDim = new Dimension(maxw, height);
      else prefDim = new Dimension(prefWidth, height);
    }
  }


  protected void maybeAddToken(StringBuffer token)
  {
    if(token.length() != 0)
    {
      tokens.addElement(token.toString());
      token.setLength(0);
    }
  }
}
