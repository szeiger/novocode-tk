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

import java.beans.IntrospectionException;
import com.novocode.tk.beans.*;
import java.io.InputStream;


public class CommandLineConfigTest
{
  private int int1;
  private String hostName;
  private boolean recursive;
  private int[] indexed;
  private String[] files;
  private InputStream in;

  public void setInt1(int i) { int1 = i; }
  public int getInt1() { return int1; }

  public void setHostName(String s) { hostName = s; }
  public String getHostName() { return hostName; }

  public void setRecursive(boolean b) { recursive = b; }
  public boolean isRecursive() { return recursive; }

  public void setIndexed(int[] i) { indexed = i; }
  public int[] getIndexed() { return indexed; }

  public void setFiles(String[] i) { files = i; }
  public String[] getFiles() { return files; }

  public void setIn(InputStream i) { in = i; }
  public InputStream getIn() { return in; }


  public static void main(String[] args) throws IntrospectionException
  {
    CommandLineConfigTest t = new CommandLineConfigTest();
    t.setIn(System.in);

    CommandLineConfigurator c = new CommandLineConfigurator(t);
    c.setConverter(InputStream.class, new FileStreamExternalFormConverter());
    c.setTopText("CommandLineConfigTest 1.3\n"+c.getTopText());
    c.addCallback("version", 'V', "display the version number and exit",
		  new Runnable(){
      public void run()
      {
	System.err.println("CommandLineConfigTest Version 1.2");
	System.err.println("Compiled with Novocode Toolkit "+
			   com.novocode.tk.Version.STATIC_VERSION);
	System.err.println("Running with Novocode Toolkit "+
			   com.novocode.tk.Version.getRuntimeVersion());
	System.exit(0);
      }
    });
    c.setAutoAddPropertyOptions(true);
    c.configure(args);

    System.out.println("------------- dump ----------");
    System.out.println("in = "+t.in);
    System.out.println("int1 = "+t.int1);
    System.out.println("hostName = "+t.hostName);
    System.out.println("recursive = "+t.recursive);
    if(t.indexed == null) System.out.println("indexed = null");
    else
    {
      for(int i=0; i<t.indexed.length; i++)
	System.out.println("indexed["+i+"] = "+t.indexed[i]);
    }
    if(t.files == null) System.out.println("files = null");
    else
    {
      for(int i=0; i<t.files.length; i++)
	System.out.println("files["+i+"] = "+t.files[i]);
    }
  }
}
