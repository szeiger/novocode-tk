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


package com.novocode.tk.awt;


/**
 * TreeView is an interface that is implemented by all classes that are
 * capable of displaying a TreeModel.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Immature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public interface TreeView
{
  /**
   * The model has changed.
   */

  public abstract void modelHasChanged(TreeModel model);
}
