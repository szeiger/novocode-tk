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


package com.novocode.tk.util;


/**
 * A Timer is a thread which invokes callback methods at specified times.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class Timer extends Thread
{
  /**
   * The callback interface. The <EM>timeReached()</EM> method is invoked
   * by the Timer when an Event occurs.
   */

  public static interface Listener
  {
    public abstract void timeReached(Event e);
  }


  /**
   * A timer event.
   */

  public static final class Event
  {
    long millis;
    Listener l;

    public Event(long millis, Listener l)
    {
      this.millis = millis;
      this.l = l;
    }

    public long getMillis() { return millis; }

    public Listener getListener() { return l; }
  }


  private Event[] a = new Event[16];
  private int n = 0;


  /**
   * Schedule an Event.
   *
   * <P><STRONG>Example:</STRONG> The following piece of code creates
   * a Timer and makes it print "Hello World" ten seconds from now.
   *
   * <PRE>
   * Timer t = new Timer();
   * t.insert(new Timer.Event(System.currentTimeMillis() + 10000,
   *                          new Timer.Listener(){
   *   public void timeReached(Timer.Event e) {
   *     System.out.println("Hello World");
   *   }}));
   * t.start();
   * </PRE>
   *
   * This method may be called at any time, before or after the Timer
   * has been started.
   */

  public synchronized void insert(Event e)
  {
    if(n >= a.length-1)
    {
      Event[] a2 = new Event[a.length*2];
      System.arraycopy(a, 0, a2, 0, a.length);
      a = a2;
    }
    int k = ++n;
    while((k>1) && (a[k/2].millis>=e.millis)) { a[k] = a[k/2]; k = k/2; }
    a[k] = e;
    interrupt();
  }


  private synchronized Event getMin()
  {
    if(n == 0) return null;
    int k = 1, j;
    Event x = a[1];
    a[1] = a[n--];
    Event w = a[1];
    while(k <= n/2)
    {
      j = k + k;
      if(j<n && a[j+1].millis<a[j].millis) j++;
      if(w.millis <= a[j].millis) break;
      a[k] = a[j];
      k = j;
    }
    a[k] = w;
    a[n+1] = null;
    return x;
  }


  //private synchronized Event peekMin()
  //{
  //  if(n == 0) return null;
  //  return a[1];
  //}


  /**
   * Creates a new Timer.
   *
   * The Thread is automatically marked as a daemon thread, but it is
   * <EM>not</EM> started.
   */

  public Timer()
  {
    setDaemon(true);
  }


  public void run()
  {
    while(true)
    {
      try
      {
	long l;
	synchronized(this)
	{
	  if(n == 0) l = Long.MAX_VALUE;
	  else l = a[1].millis - System.currentTimeMillis();
	}
	if(l > 0) sleep(l);
	synchronized(this)
	{
	  while(n != 0) if(a[1].millis <= System.currentTimeMillis())
	    a[1].l.timeReached(getMin()); else break;
	}
      }
      catch(InterruptedException e) {}
    }
  }
}
