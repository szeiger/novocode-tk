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
import com.novocode.tk.io.*;


/**
 * An external form converter which allows <EM>NFile</EM> objects to be
 * created by specifying a file name for a <EM>FileNFile</EM> object.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class FileNFileExternalFormConverter extends ExternalFormConverter
{
  /**
   * @return "FILE".
   */

  public String getExternalTypeName(Class type)
  {
    if(type != NFile.class) throw new IllegalArgumentException();
    return "FILE";
  }


  /**
   * @return "<none>" if the value is null, "?" if the value is not a
   *         <CODE>FileNFile</CODE>, otherwise the <CODE>FileNFile</CODE>'s
   *         name as returned by <CODE>toString()</CODE>.
   */

  public String toExternalForm(Class type, Object value)
      throws IllegalArgumentException
  {
    if(type != NFile.class) throw new IllegalArgumentException();

    if(value == null) return "<none>";

    if(value instanceof FileNFile) return value.toString();
    else return "?";
  }


  /**
   * @return <EM>null</EM> if the external form is an empty string, otherwise
   *         a <CODE>FileNFile</CODE> object for the specified local file.
   */

  public Object toObject(Class type, String external)
      throws IllegalArgumentException, InvocationTargetException
  {
    if(type != NFile.class) throw new IllegalArgumentException();

    if(external.length() == 0) return null;

    return new FileNFile(external);
  }
}
