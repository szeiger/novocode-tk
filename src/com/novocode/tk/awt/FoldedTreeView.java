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

import java.util.Vector;
import java.util.Hashtable;
import java.awt.*;
import java.awt.event.*;


/**
 * A FoldedTreeView is an AWT Component for displaying a TreeModel in which
 * branches of the tree can be folded by the user.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Immature.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class FoldedTreeView extends Canvas implements TreeView
{
  private static final Color lightYellow = new Color(0xFF, 0xFF, 0xCC);


  private TreeModel model;
  private Color lineColor;
  private FontMetrics fm;
  private int lineHeight, boxSize;
  private boolean layoutDirty = true;
  private Dimension minDim = new Dimension(50,20), prefDim = new Dimension();
  private Insets insets = new Insets(0,0,0,0);
  private Vector nodes = new Vector();
  private Hashtable boxes = new Hashtable();
  private Dimension dim;
  private TreeModel.Node selectedNode;


  /**
   * Creates a new FoldedTreeView for the specified model.
   */

  public FoldedTreeView(TreeModel model)
  {
    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    this.model = model;
  }


  public void addNotify()
  {
    super.addNotify();
    model.addView(this);
    Color fg = getForeground(), bg = getBackground();
    lineColor = new Color(bg.getRed() + (fg.getRed() - bg.getRed())/2,
			  bg.getGreen() + (fg.getGreen() - bg.getGreen())/2,
			  bg.getBlue() + (fg.getBlue() - bg.getBlue())/2);
    fm = getFontMetrics(getFont());
    lineHeight = fm.getHeight();
    boxSize = (fm.getAscent() - fm.getDescent()) / 2;
  }


  public void removeNotify()
  {
    model.removeView(this);
    super.removeNotify();
  }


  public Dimension getMinimumSize() { return minDim; }


  public synchronized Dimension getPreferredSize()
  {
    if(layoutDirty)
    {
      prefDim.width = prefDim.height = 0;
      TreeModel.Node root = model.getRoot();
      if(root != null) calculateNodeSize(root, 3*boxSize + 1, prefDim);
      prefDim.width += insets.left + insets.right;
      prefDim.height += insets.top + insets.bottom;
      layoutDirty = false;
    }
    return prefDim;
  }


  private void calculateNodeSize(TreeModel.Node n, int off, Dimension dim)
  {
    int w = fm.stringWidth(n.getName()) + off;
    if(w > dim.width) dim.width = w;
    dim.height += lineHeight;
    if(n.isExpanded())
    {
      TreeModel.Node[] na = n.getChildren();
      if(na != null)
	for(int i=0; i<na.length; i++)
	  calculateNodeSize(na[i], off + 4*boxSize + 1, dim);
    }
  }


  public synchronized void paint(Graphics g)
  {
    nodes.setSize(0);
    boxes.clear();
    dim = getSize();
    drawNode(model.getRoot(), new Point(insets.left, insets.top), g);
  }


  private boolean drawNode(TreeModel.Node n, Point p, Graphics g)
  {
    if(n.isSelected())
    {
      g.setColor(lightYellow);
      g.fillRect(0, p.y, dim.width, lineHeight);
    }

    g.setColor(getForeground());
    g.drawString(n.getName(), p.x + 3*boxSize + 1, p.y + fm.getMaxAscent());
    nodes.addElement(n);

    TreeModel.Node[] na = n.getChildren();
    int midy = p.y + lineHeight/2;

    if(na == null)
    {
      g.setColor(lineColor);
      g.drawLine(p.x + boxSize, midy, p.x + 3*boxSize - 1, midy);
      p.y += lineHeight;
    }
    else
    {
      boolean exp = n.isExpanded();
      boxes.put(n,
		new Rectangle(p.x, midy - boxSize, 2*boxSize+1, 2*boxSize+1));
      drawBox(g, p.x, midy - boxSize, 2*boxSize, 2*boxSize, !exp);
      g.setColor(lineColor);
      g.drawLine(p.x + 2*boxSize + 1, midy, p.x + 3*boxSize - 1, midy);
      p.y += lineHeight;
      if(exp)
      {
	int oldx = p.x, oldy = p.y;
	p.x += 4*boxSize + 1;
	int linex = oldx + 5*boxSize + 1, oldliney = oldy;
	for(int i=0; i<na.length; i++)
	{
	  int top = p.y;
	  if(drawNode(na[i], p, g))
	  {
	    int liney = top + boxSize - 2;
	    g.drawLine(linex, oldliney, linex, liney);
	    oldliney = liney + 2*boxSize + 2;
	  }
	  else
	  {
	    int liney = p.y - lineHeight + 2*boxSize - 1;
	    g.drawLine(linex, oldliney, linex, liney);
	    oldliney = liney;
	  }
	}
	p.x = oldx;
      }
    }

    return na != null;
  }


  private void drawBox(Graphics g, int x, int y, int w, int h, boolean plus)
  {
    g.setColor(Color.white);
    g.fillRect(x, y, w, h);
    g.setColor(Color.black);
    g.drawRect(x, y, w, h);
    g.drawLine(x+2, y+h/2, x+w-2, y+h/2);
    if(plus) g.drawLine(x+w/2, y+2, x+w/2, y+h-2);
  }


  /**
   * The TreeModel has changed and the tree should thus be redrawn.
   */

  public void modelHasChanged(TreeModel model)
  {
    layoutDirty = true;
    if(isShowing())
    {
      repaint();
      getParent().invalidate();
      getParent().validate();
    }
  }


  public void setInsets(Insets i) { insets = i; }


  protected void processMouseEvent(MouseEvent e)
  {
    if(e.getID() == MouseEvent.MOUSE_PRESSED &&
       ((e.getModifiers() &
	 (MouseEvent.BUTTON2_MASK|MouseEvent.BUTTON3_MASK)) == 0))
    {
      int x = e.getX(), y = e.getY();
      boolean doubleClick = e.getClickCount() > 1;
      int line = (y - insets.top)/lineHeight;
      if(y >= insets.top && line < nodes.size())
      {
	TreeModel.Node n = (TreeModel.Node)nodes.elementAt(line);
	//System.out.println(line+": "+n.getName());
	Rectangle box = (Rectangle)boxes.get(n);
	boolean b = n.getChildren() != null;
	if(b && box != null && box.contains(x,y))
	{
	  n.setExpanded(!n.isExpanded());
	  model.treeHasChanged();
	}
	else
	{
	  if((!model.isMultiselectable()) && selectedNode != null)
	    selectedNode.setSelected(false);

	  n.setSelected(!n.isSelected());
	  if(b && doubleClick) n.setExpanded(!n.isExpanded());
	  model.treeHasChanged();

	  if(!model.isMultiselectable()) selectedNode = n;
	}
      }
    }
    else super.processMouseEvent(e);
  }
}
