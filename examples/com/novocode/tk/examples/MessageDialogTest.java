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


package com.novocode.tk.examples;

import java.awt.Frame;
import com.novocode.tk.awt.MessageDialog;


public class MessageDialogTest
{
  public static void main(String[] args)
  {
    Frame f = new Frame();
    f.setSize(100,100);
    f.setVisible(true);
    MessageDialog.show(f, "Test", "Something happened.", "OK");
    System.out.println(MessageDialog.ask(f, "Question",
					 "Really change system?",
					 "Yes", "No"));
  }
}
