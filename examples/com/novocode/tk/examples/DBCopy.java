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


public class DBCopy
{
  private static String[] args;
  private static int argp;


  public static void main(String[] argv) throws Exception
  {
    NFile src = null, dst = null;

    args = argv;
    if(args.length == 0) showOptions();

    for(argp=0; argp<args.length; argp++)
    {
      if(args[argp].equals("-h") || args[argp].equals("?")) showOptions();
      else if(args[argp].equals("-p")) Class.forName(args[++argp]);
      else if(args[argp].equals("-s")) src = getFile();
      else if(args[argp].equals("-d")) dst = getFile();
      else fail("Illegal argument: "+args[argp]);
    }

    if(src == null) fail("No source specified");
    if(src == null) fail("No destination specified");

    src.copyTo(dst);
    System.exit(0);
  }


  private static NFile getFile() throws NFileException
  {
    String url = args[++argp];
    if(url.startsWith("file:")) return new FileNFile(url.substring(5));
    else return new JdbcNFileManager(url, args[++argp], args[++argp],
				     args[++argp]).getNFile(args[++argp]);
  }


  private static void showOptions()
  {
    System.err.println
      ("Options:\n"+
       "  -h, ?                                      Show this message\n"+
       "  -s <url> [<user> <passwd> <table> <file>]  Source\n"+
       "  -d <url> [<user> <passwd> <table> <file>]  Destination\n"+
       "  -p <class>                                 Preload a class");
    System.exit(0);
  }


  private static void fail(String s)
  {
    System.err.println(s);
    System.exit(1);
  }
}
