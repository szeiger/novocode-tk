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


package com.novocode.tk.util;

import java.lang.reflect.*;
import java.util.Properties;


/**
 * A utility class that can be used to start an application and set
 * system properties via the "-D" option if the JVM doesn't support "-D".
 *
 * <P><STRONG>Example:</STRONG> Where you would normally start an
 * application with
 * "java -Dfoo=bar MyClass arg1 arg2" you can use
 * "java com.novocode.tk.util.Wrapper -Dfoo=bar MyClass arg1 arg2" if the
 * <i>java</i> command does not understand the "-D" option.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Mature. Fixed API. Fully documented. This class is used directly
 * by the user of a Java application and not by an application
 * programmer, so it does not really belong into this toolkit.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class Wrapper
{
  /* Dummy constructor */
  private Wrapper() {}


  public static void main(String[] args)
  {
    Properties sys = System.getProperties();
    String clname;
    int i;

    for(i=0; i<args.length; i++)
    {
      if(args[i].startsWith("-D"))
      {
	int pos = args[i].indexOf('=');
	if(pos == -1) fail("Illegal argument "+args[i]);
	String name = args[i].substring(2, pos), value = "";
	if(pos != (args[i].length()-1)) value = args[i].substring(pos+1);
	sys.put(name, value);
      }
      else break;
    }

    if(i >= args.length) fail("No class given");
    clname = args[i++];

    String[] pargs = new String[args.length-i];
    for(int j=0; j<pargs.length; j++, i++) pargs[j] = args[i];

    Method meth = null;
    try
    {
      Class cl = Class.forName(clname);
      meth = cl.getDeclaredMethod("main", new Class[] {String[].class});
    }
    catch(Exception e) { fail(e.toString()); }

    try
    {
      meth.invoke(null, new Object[] { pargs });
    }
    catch(IllegalAccessException e) { fail(e.toString()); }
    catch(IllegalArgumentException e) { fail(e.toString()); }
    catch(InvocationTargetException e)
    {
      e.getTargetException().printStackTrace(System.err);
      System.exit(1);
    }
  }


  private static void fail(String s)
  {
    System.err.println("Wrapper: "+s);
    System.exit(1);
  }
}
