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
 * An external form converter which substitues a subtype
 * for a bean argument type.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class SubstituteExternalFormConverter extends ExternalFormConverter
{
  private ExternalFormConverter parent;
  Class beanType, externalType;


  public SubstituteExternalFormConverter(ExternalFormConverter parent,
					 Class beanType, Class externalType)
  {
    if(!beanType.isAssignableFrom(externalType))
      throw new IllegalArgumentException
	("externalType must be a subclass of beanType");
    this.parent = parent;
    this.beanType = beanType;
    this.externalType = externalType;
  }


  public String getExternalTypeName(Class type)
  {
    if(type == beanType) type = externalType;
    return parent.getExternalTypeName(type);
  }


  public String toExternalForm(Class type, Object value)
      throws IllegalArgumentException
  {
    if(type == beanType)
    {
      type = externalType;
      if((value != null) && (!type.isInstance(value))) return "?";
    }
    return parent.toExternalForm(type, value);
  }


  public Object toObject(Class type, String external)
      throws IllegalArgumentException, InvocationTargetException
  {
    if(type == beanType) type = externalType;
    return parent.toObject(type, external);
  }
}
