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

import java.io.*;

import com.novocode.tk.util.ByteString;
import com.novocode.tk.io.ByteStringInputStream;


/**
 * ByteStringTest is a benchmark for the ByteString and
 * ByteStringInputStream classes.
 *
 * You should use a relatively long text file as an input file to get
 * reliable results. Here are the results I produced with JDK1.1.3
 * under Linux on my PC:
 *
 * <PRE>
 * <FONT COLOR="#FF0000"># ls -l intuition.doc</FONT>
 * -rw-r--r--   1 szeiger  root       283460 Jul 28 01:57 intuition.doc
 *
 * <FONT COLOR="#FF0000"># java ByteStringTest intuition.doc /tmp/ByteStringTest-out</FONT>
 * Read and write with String:
 * 7.842
 *
 * Read and write with ByteString:
 * 1.313
 *
 * Read and write directly:
 * 0.077
 *
 * Read, replace and write with String:
 * 7.537
 *
 * Read, replace and write with ByteString:
 * 1.276
 *
 * Read, replace and write directly:
 * 0.304
 * </PRE>
 *
 * @author Stefan Zeiger
 */

public class ByteStringTest
{
  public static void main(String[] args) throws IOException
  {
    long t0, f0;
    Runtime rt = Runtime.getRuntime();

    if(args.length != 2)
    {
      System.err.println("Syntax: java ByteStringTest <input file name> "+
			 "<output file base name>");
      System.exit(1);
    }

    rt.gc();

    {
      System.out.println("\nRead and write with String:");
      t0 = System.currentTimeMillis();
      DataInputStream in =
	new DataInputStream(new FileInputStream(args[0]));
      PrintStream out = new PrintStream(new FileOutputStream(args[1]+"rws"));
      String line;
      while((line = in.readLine()) != null) out.println(line);
      in.close();
      out.close();
    }
    rt.gc();
    System.out.println(((double)(System.currentTimeMillis()-t0))/1000.0);

    {
      System.out.println("\nRead and write with ByteString:");
      t0 = System.currentTimeMillis();
      ByteStringInputStream in =
	new ByteStringInputStream(new FileInputStream(args[0]),256);
      OutputStream out = new FileOutputStream(args[1]+"rwbs");
      ByteString line = new ByteString();
      while((in.viewLine(line)) != null) line.printlfTo(out);
      in.close();
      out.close();
    }
    rt.gc();
    System.out.println(((double)(System.currentTimeMillis()-t0))/1000.0);

    {
      System.out.println("\nRead and write directly:");
      t0 = System.currentTimeMillis();
      File f = new File(args[0]);
      byte[] buf = new byte[(int)f.length()];
      InputStream in = new FileInputStream(args[0]);
      OutputStream out = new FileOutputStream(args[1]+"rwd");
      in.read(buf,0,buf.length);
      out.write(buf,0,buf.length);
      in.close();
      out.close();
    }
    rt.gc();
    System.out.println(((double)(System.currentTimeMillis()-t0))/1000.0);

    {
      System.out.println("\nRead, replace and write with String:");
      t0 = System.currentTimeMillis();
      DataInputStream in =
	new DataInputStream(new FileInputStream(args[0]));
      PrintStream out = new PrintStream(new FileOutputStream(args[1]+"rrws"));
      String line;
      while((line = in.readLine()) != null)
	out.println(line.replace('e','E'));
      in.close();
      out.close();
    }
    rt.gc();
    System.out.println(((double)(System.currentTimeMillis()-t0))/1000.0);

    {
      System.out.println("\nRead, replace and write with ByteString:");
      t0 = System.currentTimeMillis();
      ByteStringInputStream in =
	new ByteStringInputStream(new FileInputStream(args[0]),256);
      OutputStream out = new FileOutputStream(args[1]+"rrwbs");
      ByteString line = new ByteString();
      while((in.viewLine(line)) != null)
	line.replaceSelf((byte)'e',(byte)'E').printlfTo(out);
      in.close();
      out.close();
    }
    rt.gc();
    System.out.println(((double)(System.currentTimeMillis()-t0))/1000.0);

    {
      System.out.println("\nRead, replace and write directly:");
      t0 = System.currentTimeMillis();
      File f = new File(args[0]);
      byte[] buf = new byte[(int)f.length()];
      InputStream in = new FileInputStream(args[0]);
      OutputStream out = new FileOutputStream(args[1]+"rrwd");
      in.read(buf,0,buf.length);
      for(int j=0; j<buf.length; j++)
	if(buf[j] == (byte)'e') buf[j] = (byte)'E';
      out.write(buf,0,buf.length);
      in.close();
      out.close();
    }
    rt.gc();
    System.out.println(((double)(System.currentTimeMillis()-t0))/1000.0);
  }
}
