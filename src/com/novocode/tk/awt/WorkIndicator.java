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
 * A WorkIndicator displays a simple animation while an application
 * is doing some work for an extended period of time.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively Mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class WorkIndicator extends Canvas
       implements ComponentListener, Runnable
{
  private static final Color flashColor1 = Color.black;
  private static final Color flashColor2 = Color.white;


  private int pos = -1, numFields, sleep;
  private Thread animator;
  private boolean shouldBeRunning;


  /**
   * Creates a new WorkIndicator with 10 fields and 100ms sleep time.
   */

  public WorkIndicator() { this(10, 100); }


  /**
   * Creates a new WorkIndicator.
   *
   * @param numFields the number of boxes in the animation.
   * @param sleep the sleep time in milliseconds between animation
   *        frames.
   */

  public WorkIndicator(int numFields, int sleep)
  {
    this.numFields = numFields;
    this.sleep = sleep;
    addComponentListener(this);
  }


  public void paint(Graphics g)
  {
    super.paint(g);
    Dimension d = getSize();
    if(pos == -1) return;
    if(pos < numFields)
    {
      g.setColor(flashColor1);
      for(int i=0; i<=pos; i++) drawField(g, d, i);
    }
    else if(pos < 2*numFields)
    {
      g.setColor(flashColor2);
      for(int i=0; i<=pos-numFields; i++) drawField(g, d, i);
      g.setColor(flashColor1);
      for(int i=pos-numFields+1; i<numFields; i++) drawField(g, d, i);
    }
    else
    {
      g.setColor(flashColor1);
      for(int i=0; i<=pos-2*numFields; i++) drawField(g, d, i);
      g.setColor(flashColor2);
      for(int i=pos-2*numFields+1; i<numFields; i++) drawField(g, d, i);
    }
  }


  private void drawField(Graphics g, Dimension d, int n)
  {
    int left = ((d.width-1)*n)/numFields;
    int nextLeft = ((d.width-1)*(n+1))/numFields;
    g.fillRect(left+1, 1, nextLeft-left-1, d.height-1);
  }


  /**
   * Starts the animation. Does nothing if it's already running.
   */

  public synchronized void start()
  {
    shouldBeRunning = true;
    checkStart();
  }


  /**
   * Stops the animation. Does nothing if it's not running.
   */

  public synchronized void stop()
  {
    shouldBeRunning = false;
    checkStop();
  }


  private synchronized void checkStart()
  {
    if((animator == null) && isVisible() && shouldBeRunning)
    {
      animator = new Thread(this);
      animator.start();
    }
  }


  private synchronized void checkStop()
  {
    if((animator != null) && (!shouldBeRunning))
    {
      animator.stop();
      animator = null;
      pos = -1;
      repaint();
    }
  }


  public void run()
  {
    while(true)
    {
      int newpos = pos + 1;
      if(newpos == 3*numFields) newpos = numFields;
      pos = newpos;
      repaint();
      try { Thread.sleep(sleep); } catch(InterruptedException ignored) {}
    }
  }


  public void componentResized(ComponentEvent e) {}

  public void componentMoved(ComponentEvent e) {}

  public void componentShown(ComponentEvent e) { checkStart(); }

  public void componentHidden(ComponentEvent e) { checkStop(); }
}
