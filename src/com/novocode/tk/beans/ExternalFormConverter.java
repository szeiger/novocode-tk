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

import java.lang.reflect.InvocationTargetException;


/**
 * This is the base class for all external form converters, classes which
 * create objects of a specific type from human-readable strings and
 * vice versa.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public abstract class ExternalFormConverter
{
  /**
   * Returns the external type name for a supported type. The default
   * implementation converts the unqualified class name (or primitive type
   * name) into an upper-case, dash-separated form, e.g.
   * <EM>java.lang.FileInputStream</EM> becomes <EM>FILE-INPUT-STREAM</EM>.
   *
   * @param type the type which should be named.
   * @return the external type name.
   * @exception IllegalArgumentException if the method was called with an
   *                                     unsupported type.
   */

  public String getExternalTypeName(Class type) throws IllegalArgumentException
  {
    String s = type.getName();
    int i = s.lastIndexOf('.');
    if(i != -1) s = s.substring(i+1);
    return CommandLineConfigurator.makeLongName(s).toUpperCase();
  }


  /**
   * Creates an object from its external form.
   *
   * @param type the type to which to convert.
   * @param external the external form to convert.
   * @return an object of the specified type or a wrapper class in case of
   *         a primitive type.
   * @exception IllegalArgumentException if the method was called with an
   *                                     unsupported type.
   * @exception InvocationTargetException if the conversion could not be
   *                                      performed.
   */

  public abstract Object toObject(Class type, String external)
      throws IllegalArgumentException, InvocationTargetException;


  /**
   * Returns the external form of an object.
   *
   * @param type the type from which to convert.
   * @param value the value to convert. This value is of the specified
   *              type or a wrapper class in case of a primitive type.
   * @return the external form of the value.
   * @exception IllegalArgumentException if the method was called with an
   *                                     unsupported type.
   */

  public abstract String toExternalForm(Class type, Object value)
      throws IllegalArgumentException;
}
