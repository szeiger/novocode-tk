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

import java.awt.*;
import java.awt.event.*;


/**
 * An ActionConfirmer is an implementation of the ActionListener
 * interface which is plugged between your real ActionListener and
 * an object which fires an ActionEvent. When the ActionConfirmer
 * receives an ActionEvent it pops up a MessageDialog to give the user a
 * choice to confirm or cancel the action.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.awt.MessageDialog
 */

public class ActionConfirmer implements ActionListener
{
  private ActionListener real;
  private String title, text, pos, neg;
  private Frame par;


  /**
   * Creates a new ActionConfirmer with the button labels "OK" and
   * "Cancel".
   *
   * @see #ActionConfirmer(java.awt.event.ActionListener, java.awt.Frame, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */

  public ActionConfirmer(final ActionListener real, final Frame par,
			 final String title, final String text)
  {
    this(real, par, title, text, "OK", "Cancel");
  }


  /**
   * Creates a new ActionConfirmer.
   *
   * @param real the ActionListener which is invoked when the user
   *        confirms the Dialog.
   * @param par the parent Frame for the Dialog. Use "new Frame()" to
   *        create a dummy Frame if you don't have one.
   * @param title the Dialog's title.
   * @param text the text which is displayed in the Dialog. Lines are
   *        separated by '\n'.
   * @param pos the text on the confirmation button.
   * @param neg the text on the cancel button.
   */

  public ActionConfirmer(final ActionListener real, final Frame par,
			 final String title, final String text,
			 final String pos, final String neg)
  {
    this.real = real;
    this.title = title;
    this.text = text;
    this.pos = pos;
    this.neg = neg;
    this.par = par;
  }


  /**
   * Opens the confirmation Dialog and forwards the ActionEvent if
   * the Dialog was confirmed by the user.
   */

  public void actionPerformed(ActionEvent e)
  {
    Button posButton = new Button(pos);
    posButton.setActionCommand(e.getActionCommand());
    MessageDialog d = new MessageDialog
      (par, title, text, neg, new Button[] { posButton });
    posButton.addActionListener(real);
    posButton.addActionListener(d);
    d.setVisible(true);
  }
}
