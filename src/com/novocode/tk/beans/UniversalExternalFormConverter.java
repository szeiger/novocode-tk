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


/**
 * The default external form converter which is used by the
 * CommandLineConfigurator class.
 *
 * External forms are created by calling the value's <EM>toString()</EM>
 * method. Objects are created by invoking a constructor that takes a
 * <EM>String</EM> argument or calling a static <EM>valueOf()</EM> method.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class UniversalExternalFormConverter extends ExternalFormConverter
{
  private static final Class[] SINGLE_STRING_ARGLIST = { String.class };


  public String toExternalForm(Class type, Object value)
      throws IllegalArgumentException
  {
    if(value == null) return "<null>";
    else return value.toString();
  }


  public Object toObject(Class type, String external)
      throws IllegalArgumentException, InvocationTargetException
  {
    if(type == String.class) return external; // a shortcut

    try
    {
      try // Look for static valueOf() method
      {
	Method converter = type.getMethod("valueOf", SINGLE_STRING_ARGLIST);
	return converter.invoke(null, new Object[] { external });
      }
      catch(NoSuchMethodException e) // Look for constructor
      {
	Constructor cons = type.getConstructor(SINGLE_STRING_ARGLIST);
	return cons.newInstance(new Object[] { external });
      }
    }
    catch(Exception e)
    {
      if(e instanceof InvocationTargetException)
	throw (InvocationTargetException)e;
      else
	throw new IllegalArgumentException(e.toString());
    }
  }
}
