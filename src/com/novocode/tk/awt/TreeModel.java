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


/**
 * A TreeModel is a model of a tree that can be visualized using a class
 * that implements the TreeView interface.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Immature.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class TreeModel
{
  public static interface Node
  {
    public abstract String getName();
    public abstract Node getParent();
    public abstract Node[] getChildren();
    public abstract void setExpanded(boolean b);
    public abstract boolean isExpanded();
    public abstract void setSelected(boolean b);
    public abstract boolean isSelected();
  }


  public static class NodeImpl implements Node
  {
    private Node[] children;
    private Vector childVector;
    private String name;
    private Node parent;
    private boolean expanded, selected, arrayDirty = true;

    public NodeImpl() {}
    public NodeImpl(String name) { this.name = name; }

    public void setName(String s) { name = s; }
    public String getName() { return name; }
    public Node getParent() { return parent; }
    public void setExpanded(boolean b) { expanded = b; }
    public boolean isExpanded() { return expanded; }
    public void setSelected(boolean b) { selected = b; }
    public boolean isSelected() { return selected; }

    public synchronized Node[] getChildren()
    {
      if(childVector == null) return null;
      else
      {
	if(arrayDirty)
	{
	  children = new Node[childVector.size()];
	  for(int i=0; i<children.length; i++)
	    children[i] = (Node)childVector.elementAt(i);
	  arrayDirty = false;
	}
	return children;
      }
    }

    public synchronized void addChild(NodeImpl child)
    {
      if(childVector == null) childVector = new Vector();
      childVector.addElement(child);
      child.parent = this;
      arrayDirty = true;
    }
  }


  private Node root;
  private Vector views = new Vector();
  private boolean multi;


  public TreeModel() {}


  public TreeModel(Node root) { this.root = root; }


  public void setMultiselectable(boolean b) { multi = b; }


  public boolean isMultiselectable() { return multi; }


  public synchronized void setRoot(Node n)
  {
    root = n;
    treeHasChanged();
  }


  public Node getRoot() { return root; }


  public synchronized void addView(TreeView v)
  {
    if(!views.contains(v)) views.addElement(v);
  }


  public synchronized void removeView(TreeView v) { views.removeElement(v); }


  public synchronized void treeHasChanged()
  {
    for(int i=views.size()-1; i>=0; i--)
      ((TreeView)views.elementAt(i)).modelHasChanged(this);
  }
}
