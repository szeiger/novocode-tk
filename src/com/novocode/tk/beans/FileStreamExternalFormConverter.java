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


package com.novocode.tk.beans;

import java.lang.reflect.*;
import java.io.*;


/**
 * An external form converter which allows the use of file names where an
 * <EM>InputStream</EM> or <EM>OutputStream</EM> is expected.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class FileStreamExternalFormConverter extends ExternalFormConverter
{
  public String getExternalTypeName(Class type)
  {
    if((type != InputStream.class) && (type != OutputStream.class))
      throw new IllegalArgumentException();
    return "FILE";
  }


  public String toExternalForm(Class type, Object value)
      throws IllegalArgumentException
  {
    if((type != InputStream.class) && (type != OutputStream.class))
      throw new IllegalArgumentException();

    if(value == null) return "<none>";

    if(type == InputStream.class)
    {
      if(value == System.in) return "-";
      else if(value instanceof FileInputStream) return value.toString();
      else return "?";
    }
    else // if(type == OutputStream.class)
    {
      if(value == System.out) return "-";
      else if(value instanceof FileOutputStream) return value.toString();
      else return "?";
    }
  }


  public Object toObject(Class type, String external)
      throws IllegalArgumentException, InvocationTargetException
  {
    if((type != InputStream.class) && (type != OutputStream.class))
      throw new IllegalArgumentException();

    if(external.length() == 0) return null;

    try
    {
      if(type == InputStream.class)
      {
	if(external.equals("-")) return System.in;
	return new FileInputStream(external);
      }
      else // if(type == OutputStream.class)
      {
	if(external.equals("-")) return System.out;
	System.runFinalizersOnExit(true); // to flush the stream
	return new FileOutputStream(external);
      }
    }
    catch(IOException e) { throw new InvocationTargetException(e); }
  }
}
