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


package com.novocode.tk;


/**
 * The Novocode Toolkit version number.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class Version
{
  /* Avoid public constructor */
  private Version() {}


  /**
   * If your compiler supports inlining of <EM>static final</EM>
   * constants, you can use STATIC_VERSION to get the version of
   * the Novocode Toolkit with which your application was
   * compiled.
   */

  public static final String STATIC_VERSION = "@@ current_version @@";


  /**
   * Returns the version of the Novocode Toolkit at run-time.
   */

  public static String getRuntimeVersion() { return STATIC_VERSION; }
}
