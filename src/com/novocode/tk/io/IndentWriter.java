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


/**
 * An IndentWriter indents all written lines with some spaces. The number
 * of spaces can be changed from line to line.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class IndentWriter extends FilterWriter
{
  private int level, increase;
  private boolean isFirstChar = true;


  public IndentWriter(Writer out, int increase)
  {
    super(out);
    this.increase = increase;
  }

  public IndentWriter(Writer out)
  {
    this(out, 2);
  }


  public void setLevel(int i) { level = i; }

  public int getLevel() { return level; }

  public void incLevel() { level++; }

  public void decLevel() { level--; }


  public void write(int c) throws IOException
  {
    if(isFirstChar)
    {
      for(int i=level*increase; i>0; i--) out.write(' ');
      isFirstChar = false;
    }
    super.write(c);
    if(c == '\n') isFirstChar = true;
  }


  public void write(char cbuf[], int off, int len) throws IOException
  {
    for(int i=off; i<off+len; i++) write(cbuf[i]);
  }


  public void write(String str, int off, int len) throws IOException
  {
    for(int i=off; i<off+len; i++) write(str.charAt(i));
  }
}
