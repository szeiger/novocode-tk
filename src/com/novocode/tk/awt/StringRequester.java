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
 * A StringRequester is a Dialog with a TextField which allows the
 * user to enter a line of text.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class StringRequester extends Dialog implements ActionListener
{
  private Panel buttonPanel, inputPanel;
  private TextComponent textComponent;
  private final Frame par;
  private final String body;
  private final Component[] buttons;


  public StringRequester(Frame par, String title, String body,
			 Component[] buttons, int len)
  {
    super(par, title, false);
    this.par = par;
    this.body = body;
    this.buttons = buttons;

    textComponent = new TextField(len);
    init();
  }


  public StringRequester(Frame par, String title, String body,
			 Component[] buttons, int width, int height)
  {
    super(par, title, false);
    this.par = par;
    this.body = body;
    this.buttons = buttons;

    textComponent = new TextArea(height, width);
    init();
  }


  private void init()
  {
    buttonPanel = new Panel();
    add(buttonPanel, "South");

    inputPanel = new Panel();
    add(inputPanel, "Center");

    Canvas textCanvas = new Canvas() {
	private Dimension dim;
	private Vector lines = new Vector();
	private int textHeight;

	{
	  FontMetrics m = par.getFontMetrics(par.getFont());
	  StringTokenizer tok = new StringTokenizer(body, "\r\n", false);
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
    add(textCanvas, "North");

    inputPanel.add(textComponent);

    ((FlowLayout)inputPanel.getLayout()).setAlignment(FlowLayout.CENTER);

    for(int i=0; i<buttons.length; i++) buttonPanel.add(buttons[i]);

    ((FlowLayout)buttonPanel.getLayout()).setAlignment
      ((buttons.length > 1) ? FlowLayout.RIGHT : FlowLayout.CENTER);

    pack();
    Dimension parDim = par.getSize(), thisDim = getSize();
    Point parLoc = par.getLocation();
    setLocation(parLoc.x + (parDim.width-thisDim.width)/2,
		parLoc.y + (parDim.height-thisDim.height)/2);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
  }


  public void actionPerformed(ActionEvent e) { dispose(); }


  protected void processWindowEvent(WindowEvent e)
  {
    if(e.getID() == WindowEvent.WINDOW_CLOSING) dispose();
    super.processWindowEvent(e);
  }


  public void setText(String text) { textComponent.setText(text); }

  public String getText() { return textComponent.getText(); }


  public TextComponent getTextComponent() { return textComponent; }
}
