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


package com.novocode.tk.security;


/**
 * A SecurityManager which delegates security checks to a Sandbox which
 * is associated with a ClassLoader that implements the SandboxProvder
 * interface.
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.security.Sandbox
 * @see com.novocode.tk.security.SandboxProvider
 */

public final class LoaderBasedSandboxSecurityManager
       extends SandboxSecurityManager
{
  protected Sandbox getSandbox()
  {
    ClassLoader loader = currentClassLoader();
    if(loader instanceof SandboxProvider)
      return ((SandboxProvider)loader).getSandbox();
    else
      return null;
  }
}
