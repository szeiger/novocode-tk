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

import com.novocode.tk.io.*;


public class NFileTest
{
  public static void main(String[] args) throws Exception
  {
    // Load JDBC driver
    Class.forName("gwe.sql.gweMysqlDriver");

    // Create the manager
    JdbcNFileManager man =
      new JdbcNFileManager("jdbc:mysql://localhost:3333/test",
			   "dbadmin", "foo42", "nfile");
    // Get the root file
    NFile root = man.getNFile("/");
    NFile nf1 = root.getSub("1");
    NFile nf2 = root.getSub("foo");

    System.out.println(root.exists());
    System.out.println(nf1.exists());
    System.out.println(nf2.exists());

    System.out.println("-----------");

    System.out.println(root.hasContent());
    System.out.println(nf1.hasContent());
    System.out.println(nf2.hasContent());
  }
}
