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


package com.novocode.tk.io;

import java.io.*;


public class NFileException extends IOException
{
  private Exception e;

  protected NFileException(Exception e)
  {
    super(e.toString());
    this.e = e;
  }

  public NFileException(String s) { super(s); }

  public Exception getRealException() { return e; }

  public void printStackTrace(PrintStream p)
  {
    super.printStackTrace(p);
    if(e != null)
    {
      p.print("...caused by: ");
      e.printStackTrace(p);
    }
  }


  public static NFileException getCascadeException(Exception e)
  {
    if(e instanceof FileNotFoundException)
      return new NFileNotFoundException(e);
    else
      return new NFileException(e);
  }
}
