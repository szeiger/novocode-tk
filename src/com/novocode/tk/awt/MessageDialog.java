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

import java.util.*;
import java.awt.*;
import java.awt.event.*;


/**
 * A MessageDialog is a Dialog with some lines of text and a row of
 * buttons.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class MessageDialog extends Dialog implements ActionListener
{
  private Panel buttonPanel;
  private Button disposeButton;


  /**
   * Creates a new non-modal MessageDialog with a single button which closes
   * the dialog.
   *
   * @see #MessageDialog(java.awt.Frame, java.lang.String, java.lang.String, java.lang.String, java.awt.Component[], boolean)"
   */

  public MessageDialog(final Frame par, final String title,
		       final String text, final String disposeButtonLabel)
  {
    this(par, title, text, disposeButtonLabel, null);
  }


  /**
   * Creates a new non-modal MessageDialog with a button which closes
   * the dialog and a row of buttons (or other AWT components) to the left of
   * the dispose button.
   *
   * @see #MessageDialog(java.awt.Frame, java.lang.String, java.lang.String, java.lang.String, java.awt.Component[], boolean)"
   */

  public MessageDialog(final Frame par, final String title,
		       final String text, final String disposeButtonLabel,
		       Component[] buttons)
  {
    this(par, title, text, disposeButtonLabel, buttons, false);
  }


  /**
   * Creates a new MessageDialog with a button which closes the dialog
   * and a row of buttons (or other AWT components) to the left of
   * the dispose button.
   *
   * @param par the parent Frame for the Dialog. Use "new Frame()" to
   *        create a dummy Frame if you don't have one.
   * @param title the Dialog's title.
   * @param text the text which is displayed in the Dialog. Lines are
   *        separated by '\n'.
   * @param disposeButtonLabel the text which is displayed on the default
   *        button; null for no default button.
   * @param buttons an array of AWT components which are displayed in
   *        the Dialog.
   * @param modal true for a modal Dialog.
   */

  public MessageDialog(final Frame par, final String title,
		       final String text, final String disposeButtonLabel,
		       Component[] buttons, boolean modal)
  {
    super(par, title, modal);

    buttonPanel = new Panel();
    add(buttonPanel, "South");

    Canvas textCanvas = new Canvas() {
      private Dimension dim;
      private Vector lines = new Vector();
      private int textHeight;

      {
	FontMetrics m = par.getFontMetrics(par.getFont());
	StringTokenizer tok = new StringTokenizer(text, "\r\n", false);
	int x = 0, y = 0, h = m.getHeight();
	while(tok.hasMoreTokens())
	{
	  String l = tok.nextToken();
	  lines.addElement(l);
	  y += h;
	  int lw = m.stringWidth(l);
	  if(lw > x) x = lw;
	}
	dim = new Dimension(x+20, y+20);
	textHeight = y;
      }

      public Dimension getMinimumSize() { return dim; }

      public Dimension getPreferredSize() { return dim; }

      public void paint(Graphics g)
      {
	Dimension dim = getSize();
	FontMetrics m = g.getFontMetrics();
	int y = (dim.height - textHeight)/2 + m.getMaxAscent();
	for(int i=0; i<lines.size(); i++)
	{
	  String l = (String)lines.elementAt(i);
	  int x = (dim.width - m.stringWidth(l))/2;
	  g.drawString(l, x, y);
	  y += m.getHeight();
	}
      }
    };
    add(textCanvas, "Center");

    int buttonCount;

    if(buttons != null)
    {
      for(int i=0; i<buttons.length; i++) buttonPanel.add(buttons[i]);
      buttonCount = buttons.length;
    }
    else buttonCount = 0;

    if(disposeButtonLabel != null)
    {
      disposeButton = new Button(disposeButtonLabel);
      buttonPanel.add(disposeButton);
      disposeButton.addActionListener(this);
      buttonCount++;
    }

    ((FlowLayout)buttonPanel.getLayout()).setAlignment
      ((buttonCount > 1) ? FlowLayout.RIGHT : FlowLayout.CENTER);

    pack();
    Dimension parDim = par.getSize(), thisDim = getSize();
    Point parLoc = par.getLocation();
    setLocation(parLoc.x + (parDim.width-thisDim.width)/2,
		parLoc.y + (parDim.height-thisDim.height)/2);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
  }


  /** Closes the dialog. */

  public void actionPerformed(ActionEvent e) { dispose(); }


  protected void processWindowEvent(WindowEvent e)
  {
    if(e.getID() == WindowEvent.WINDOW_CLOSING) dispose();
    else super.processWindowEvent(e);
  }


  /**
   * Gives the focus to the default button (when possible); does
   * nothing when the default button does not exist or is not
   * visible.
   */

  public void requestDisposeButtonFocus()
  {
    if((disposeButton != null) && isVisible()) disposeButton.requestFocus();
  }


  /**
   * Creates a new MessageDialog, makes it visible and gives the
   * focus to the button.
   *
   * @see #MessageDialog(java.awt.Frame, java.lang.String, java.lang.String, java.lang.String)"
   */

  public static void show(final Frame par, final String title,
			  final String text, final String disposeButtonLabel)
  {
    MessageDialog m = new MessageDialog(par, title, text, disposeButtonLabel);
    m.setVisible(true);
    m.requestDisposeButtonFocus();
  }


  /**
   * Creates a new modal MessageDialog with two buttons, makes it visible
   * and waits for the user to press a button.
   *
   * @return <em>true</em> if the OK button was pressed;
   *         <em>false</em> for the Cancel button.
   */

  public static boolean ask(final Frame par, final String title,
			    final String text, final String okButtonLabel,
			    final String cancelButtonLabel)
  {
    final boolean[] result = new boolean[1];
    final Button okButton = new Button(okButtonLabel);
    final Button cancelButton = new Button(cancelButtonLabel);
    final MessageDialog m =
      new MessageDialog(par, title, text, null,
			new Button[] { okButton, cancelButton }, true);
    okButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e)
	{
	  result[0] = true;
	  m.dispose();
	}
      });
    cancelButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e)
	{
	  m.dispose();
	}
      });
    m.setVisible(true);
    return result[0];
  }
}
