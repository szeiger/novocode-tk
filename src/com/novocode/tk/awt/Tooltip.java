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

import com.novocode.tk.util.Timer;


/**
 * A Tooltip is a pop-up help window which can be attached to any
 * AWT component. When the mouse pointer stays within such a component
 * for an extended period of time, the help window is shown. When the mouse
 * pointer leaves the component, the help window is hidden.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class Tooltip extends Window
       implements MouseListener, ContainerListener, Timer.Listener
{
  private static final Color lightYellow = new Color(0xFF, 0xFF, 0xCC);
  private static Timer timer;
  private static Tooltip currentTooltip;
  private static Object currentTooltipMonitor = new Object();

  private static final synchronized void createTimer()
  {
    if(timer == null)
    {
      timer = new Timer();
      timer.start();
    }
  }


  private Component comp;
  private boolean layedOut;
  private FlowText ft;
  private Timer.Event event;


  /**
   * Creates a new Tooltip and attaches it to a Component.
   *
   * @param comp the AWT Component to which the Tooltip is attached.
   * @param ft a FlowText which is displayed in the Tooltip
   * @param parent the parent Frame of the Component.
   */

  public Tooltip(Component comp, FlowText ft, Frame parent)
  {
    super(parent);
    createTimer();
    this.comp = comp;
    setForeground(Color.black);
    setBackground(lightYellow);
    this.ft = ft;
    ft.setOptimizeWidth(true);
    setLayout(new GridBagLayout());
    registerComponent(comp);
  }


  public void paint(Graphics g)
  {
    super.paint(g);
    Dimension d = getSize();
    g.drawRect(0, 0, d.width-1, d.height-1);
  }


  public void dispose()
  {
    unregisterComponent(comp);
    super.dispose();
  }


  private void checkLayout()
  {
    if(!layedOut)
    {
      Font f = new Font("sansserif", Font.PLAIN, getFont().getSize());
      setFont(f);
      GridBagConstraints con = new GridBagConstraints();
      int i = getFontMetrics(f).getHeight() / 4;
      con.insets = new Insets(i,i*2,i,i*2);
      con.fill = GridBagConstraints.BOTH;
      con.weightx = 1.0;
      con.weighty = 1.0;
      ((GridBagLayout)getLayout()).setConstraints(ft, con);
      add(ft);
      setSize(getPreferredSize());
      layedOut = true;
    }
  }


  public Dimension getMinimumSize() { return getPreferredSize(); }


  public void mouseEntered(MouseEvent e)
  {
    event = new Timer.Event(System.currentTimeMillis()+400, this);
    timer.insert(event);
  }


  public void mouseExited(MouseEvent e)
  {
    Component src = (Component)e.getSource();
    if(src == comp) hideTooltip();
    else
    {
      int x = e.getX(), y = e.getY();
      for(Component c=src; c!=comp && c!=null; c=c.getParent())
      {
	Point p = c.getLocation();
	x += p.x;
	y += p.y;
      }
      if(!comp.contains(x, y)) hideTooltip();
    }
  }


  private void registerComponent(Component comp)
  {
    comp.addMouseListener(this);
    if(comp instanceof Container)
    {
      ((Container)comp).addContainerListener(this);
      Component[] comps = ((Container)comp).getComponents();
      if(comps != null)
	for(int i=0; i<comps.length; i++) registerComponent(comps[i]);
    }
  }


  private void unregisterComponent(Component comp)
  {
    comp.removeMouseListener(this);
    if(comp instanceof Container)
    {
      ((Container)comp).removeContainerListener(this);
      Component[] comps = ((Container)comp).getComponents();
      if(comps != null)
	for(int i=0; i<comps.length; i++) unregisterComponent(comps[i]);
    }
  }


  public void componentAdded(ContainerEvent e)
  {
    registerComponent(e.getChild());
  }


  public void componentRemoved(ContainerEvent e)
  {
    unregisterComponent(e.getChild());
  }


  public void timeReached(Timer.Event e)
  {
    if(event == e)
    {
      checkLayout();
      Point p = comp.getLocationOnScreen();
      Dimension cd = comp.getSize(), sd = getToolkit().getScreenSize(),
	td = getSize();
      int x = p.x + cd.width/2, y = p.y + cd.height + 8;
      if(y + td.height > sd.height) y = p.y - td.height - 8;
      if(x + td.width > sd.width) x -= td.width;
      setLocation(x,y);
      synchronized(currentTooltipMonitor)
      {
	if(currentTooltip != null) currentTooltip.hideTooltip();
	currentTooltip = this;
	setVisible(true);
      }
    }
  }


  private void hideTooltip()
  {
    synchronized(currentTooltipMonitor)
    {
      event = null;
      setVisible(false);
      currentTooltip = null;
    }
  }


  public void mouseClicked(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
}
