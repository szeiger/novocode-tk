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

import java.awt.*;
import java.awt.event.*;

import com.novocode.tk.awt.TreeModel;
import com.novocode.tk.awt.FoldedTreeView;
import com.novocode.tk.io.NFile;
import com.novocode.tk.io.JdbcNFileManager;


/** DBExplorer opens a Frame with a FoldedTreeView that displays a
 * database directory tree. Directory scanning is deferred until the
 * directory needs to be displayed.
 *
 * @author Stefan Zeiger
 */

public class DBExplorer
{
  private static class NFileNode extends TreeModel.NodeImpl
  {
    private NFile file;
    private boolean scanned;

    NFileNode(NFile f)
    {
      file = f;
      String name = f.getName();
      try
      {
	if(f.isListable() && !name.endsWith("/")) name += "/";
      }
      catch(Exception e) { e.printStackTrace(); }
      setName(name);
    }

    public synchronized TreeModel.Node[] getChildren()
    {
      if(!scanned)
      {
	try
	{
	  NFile[] a = file.listFiles();
	  if(a != null)
	    for(int i=0; i<a.length; i++) addChild(new NFileNode(a[i]));
	  scanned = true;
	}
	catch(Exception e) { e.printStackTrace(); }
      }
      return super.getChildren();
    }
  }


  public static void main(String[] args) throws Exception
  {
    if(args.length != 6)
    {
      System.err.println("Syntax: java DBExplorer <driver> <url> <table> "+
			 "<login> <password> <dir>");
      System.exit(1);
    }

    Class.forName(args[0]);
    JdbcNFileManager man =
      new JdbcNFileManager(args[1], args[3], args[4], args[2]);

    NFile nfile = man.getNFile(args[5]);
    TreeModel.Node root = new NFileNode(nfile);
    root.setSelected(true);
    TreeModel model = new TreeModel(root);

    final Frame f = new Frame("DBExplorer: "+nfile.getCanonicalPath());
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
}
