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
 * ClassLoaders which are to be managed by a LoaderBasedSandboxSecurityManager
 * need to implement this interface.
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.security.LoaderBasedSandboxSecurityManager
 */

public interface SandboxProvider
{
  public abstract Sandbox getSandbox();
}
