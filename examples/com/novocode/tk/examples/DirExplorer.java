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


package com.novocode.tk.examples;

import java.io.File;
import java.awt.*;
import java.awt.event.*;

import com.novocode.tk.awt.TreeModel;
import com.novocode.tk.awt.FoldedTreeView;


/** DirExplorer opens a Frame with a FoldedTreeView that displays a
 * directory tree. Directory scanning is deferred until the directory
 * needs to be displayed.
 *
 * @author Stefan Zeiger
 */

public class DirExplorer
{
  private static class FileNode extends TreeModel.NodeImpl
  {
    private File file;
    private boolean scanned;

    FileNode(File f)
    {
      file = f;
      String name = f.getName();
      if(f.isDirectory()) name += "/";
      else if(!f.isFile()) name += "*";
      setName(name);
    }

    public synchronized TreeModel.Node[] getChildren()
    {
      if(!scanned)
      {
	String[] sa = file.list();
	if(sa != null)
	  for(int i=0; i<sa.length; i++)
	    addChild(new FileNode(new File(file, sa[i])));
	scanned = true;
      }
      return super.getChildren();
    }
  }


  public static void openFrame(TreeModel model, String title)
  {
    final Frame f = new Frame(title);
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e)
      {
	f.dispose();
	System.exit(0);
      }
    });
    FoldedTreeView t = new FoldedTreeView(model);
    ScrollPane sp = new ScrollPane();
    sp.add(t);
    t.setInsets(new Insets(4,4,4,4));
    f.add(sp, "Center");
    f.setSize(400,400);
    f.setVisible(true);
  }


  public static void main(String[] args)
  {
    if(args.length != 1)
    {
      System.err.println("Syntax: java DirExplorer <dir>");
      System.exit(1);
    }
    File file = new File(args[0]);
    TreeModel.Node root = new FileNode(file);
    root.setSelected(true);
    TreeModel model = new TreeModel(root);
    openFrame(model, "DirExplorer: "+file.getAbsolutePath());
  }
}
