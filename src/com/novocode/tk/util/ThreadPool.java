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
 * A ThreadPool object manages a number of Threads and provides a
 * method for running a Runnable object in one of them. The Threads
 * are reused and the number of Threads in the pool is adapted
 * automatically.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class ThreadPool
{
  ThreadGroup group;
  volatile PooledThread free;
  volatile int freeNum, usedNum, max, ttl, min, pause;
  volatile Thread coll;


  /**
   * Creates a new ThreadPool with default parameters.
   *
   * The created pool has a minimum number of 10 Threads and no maximum
   * number. The thread collector is activated every minute.
   * Unused Threads expire after two minutes.
   */

  public ThreadPool() { this(null, 10, -1, 120000, 60000); }


  /**
   * Creates a new ThreadPool.
   *
   * @param group the ThreadGroup in which new Threads are created.
   * @param min the minimum number of Threads in the pool. The pool
   *        always starts with 0 Threads and creates new Threads on
   *        demand, but the thread collector never deletes
   *        Threads below the minimum number.
   * @param max the maximum number of Threads. Set to -1 to disable.
   * @param ttl the Time To Live in milliseconds for excessive Threads.
   *        The thread collector deletes a Thread that has been
   *        idle for the specified time. Set to -1 to disable the
   *        the thread collector.
   * @param pause the thread collector is activated every <EM>pause</EM>
   *        milliseconds.
   */


  public ThreadPool(ThreadGroup group, int min, int max, int ttl, int pause)
  {
    this.group = group;
    this.min = min;
    this.max = max;
    this.pause = pause;
    setTTL(ttl);
  }


  /**
   * Starts a Runnable's <EM>run()</EM> method in a free Thread.
   *
   * @exception IllegalThreadStateException if there is no free Thread
   *            available and no new Threads can be created because the
   *            allowed maximum number is exceeded.
   *
   * @param r the Runnable to be started.
   */

  public void start(Runnable r) throws IllegalThreadStateException
  {
    PooledThread t;
    boolean start;

    synchronized(this)
    {
      if(free != null)
      {
	t = free;
	free = t.next;
	freeNum--;
	t.shouldRun = true;
	start = false;
      }
      else
      {
	if((max != -1) && (freeNum + usedNum >= max))
	  throw new IllegalThreadStateException("Maximum ThreadPool capacity "+
						"exceeded.");
	t = new PooledThread(group);
	start = true;
      }
      usedNum++;
    }

    t.runnable = r;

    if(start) t.start();
    else synchronized(t) { t.notify(); }
  }


  void free(PooledThread p)
  {
    p.timeOfDeath = System.currentTimeMillis();

    synchronized(this)
    {
      usedNum--;
      freeNum++;
      p.next = free;
      free = p;
    }
  }


  public void finalize()
  {
    if(coll != null) try { coll.stop(); } catch(Throwable ignored) {}
  }


  private synchronized void collect()
  {
    if(freeNum + usedNum > min)
    {
      long rip = System.currentTimeMillis() - ttl;
      PooledThread prev = null, p = free;
      for(int i=0; i<min-usedNum; i++) p = p.next;
      while(p != null)
      {
	if(p.timeOfDeath <= rip)
	{
	  for(PooledThread q = p; q != null; q = q.next)
	  {
	    try { q.stop(); } catch(Throwable ignored) {}
	    freeNum--;
	  }
	  if(p == free) free = null;
	  else prev.next = null;
	  break;
	}
	prev = p;
	p = p.next;
      }
    }

    /*
     * System.err.println("Collector report at "+
     * new java.util.Date().toGMTString()+":");
     * System.err.println("  Used: "+usedNum+", Free: "+freeNum);
     */
  }


  /**
   * Sets the allowed minimum number of Threads in the pool. If you lower
   * this number below the current number of Threads, excessive Threads
   * are not removed until the next thread collection happens. This number
   * is meaningless if the thread collector is disabled. If you raise the
   * minimum number above the current number of Threads, no new Threads
   * are created until they are actually needed.
   *
   * @param min the new allowed minimum number of Threads.
   */

  public synchronized void setMin(int min)
  {
    this.min = min;
  }


  /**
   * Returns the allowed minimum number of Threads in the pool.
   *
   * @return the allowed minimum number of Threads.
   */

  public int getMin()
  {
    return min;
  }


  /**
   * Sets the allowed maximum number of Threads in the pool. If you lower
   * this number below the current number of Threads, no existing Threads
   * are deleted, but no new Threads will be created.
   *
   * @param max the new allowed maxmimum number of Threads or -1 for no
   *        maximum.
   */

  public synchronized void setMax(int max)
  {
    this.max = max;
  }


  /**
   * Returns the allowed maximum number of Threads in the pool.
   *
   * @return the allowed maximum number of Threads.
   */

  public int getMax()
  {
    return max;
  }


  /**
   * Sets the Time To Live for excessive Threads. The thread collector
   * deletes a Thread that has been idle for the specified time.
   *
   * @param ttl the Time To Live in milliseconds or -1 to disable the
   *            thread collector.
   */

  public synchronized void setTTL(final int ttl)
  {
    this.ttl = ttl;
    if(ttl == -1)
    {
      if(coll != null)
      {
	coll.stop();
	coll = null;
      }
    }
    else
    {
      if(coll == null)
      {
	coll = new ThreadCollector();
	int pri = coll.getPriority()+10;
	coll.setPriority(pri>Thread.MAX_PRIORITY ? Thread.MAX_PRIORITY : pri);
	coll.setDaemon(true);
	coll.start();
      }
    }
  }


  /**
   * Returns the Time To Live for excessive Threads.
   *
   * @return the Time To Live in milliseconds or -1 if the thread collector
   *         is disabled.
   */

  public int getTTL()
  {
    return ttl;
  }


  /**
   * Sets the thread collector's pause time. The thread collector is
   * activated after the specified time has elapsed. This setting is
   * meaningless if the thread collector is disabled. The new pause
   * time is used after the next awakening of the thread collector.
   *
   * @param pause the thread collector's pause time in milliseconds.
   */

  public void setPause(int pause)
  {
    this.pause = pause;
  }


  /**
   * Returns the thread collector's pause time.
   *
   * @return the thread collector's pause time in milliseconds.
   */

  public int getPause()
  {
    return pause;
  }


  private final class PooledThread extends Thread
  {
    volatile boolean shouldRun;
    volatile Runnable runnable;
    volatile PooledThread next;
    volatile long timeOfDeath;

    PooledThread(ThreadGroup g) { super(g, (Runnable)null); }

    public void run()
    {
      while(true)
      {
	try { runnable.run(); }
	catch(Throwable e)
	{
	  getThreadGroup().uncaughtException(this, e);
	}
	runnable = null;
	free(this);
	while(!shouldRun)
	{
	  try
	  {
	    synchronized(this) { wait(); }
	  }
	  catch(InterruptedException ignored) {}
	}
	shouldRun = false;
      }
    }
  }


  private final class ThreadCollector extends Thread
  {
    public void run()
    {
      while(true)
      {
	try { sleep(pause); }
	catch(InterruptedException ignored) {}
	collect();
      }
    }
  }
}
