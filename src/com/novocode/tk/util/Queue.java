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
 * A thread-safe Queue which uses a ring buffer.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class Queue
{
  private Object[] data;
  private int first;
  private volatile int size;


  /**
   * Create a new Queue with a default buffer size of 16.
   */

  public Queue()
  {
    data = new Object[16];
  }


  /**
   * Create a new Queue with the given buffer size.
   *
   * @param bufferSize the initial buffer size for the queue.
   */

  public Queue(int bufferSize)
  {
    data = new Object[bufferSize];
  }


  /**
   * Add an object at the end of the queue.
   * The buffer size is doubled if the current buffer is already full.
   *
   * @param o the non-null Object to enqueue.
   */

  public void enqueue(Object o)
  {
    if(o == null) throw new NullPointerException("Can't enqueue \"null\".");
    synchronized(this)
    {
      if(size == data.length) // buffer is full -> double size
      {
	Object[] newdata = new Object[size * 2];
	int len1 = data.length - first;
	System.arraycopy(data, first, newdata, 0, len1);
	if(first != 0) System.arraycopy(data, 0, newdata, len1, first);
	data = newdata;
	first = 0;
      }
      data[(first + size) % data.length] = o;
      size++;
      notify();
    }
  }


  /**
   * Check if the queue is empty.
   *
   * @return true if the queue is empty; otherwise false.
   */

  public boolean isEmpty()
  {
    return size == 0;
  }


  /**
   * Remove the first object from the queue and return it.
   * If the queue is empty, this method waits until a new object is
   * enqueued. If multiple Threads are calling dequeue on an empty queue,
   * the Thread which dequeues the next available object is selected
   * randomly.
   *
   * @return a dequeued object.
   */

  public Object dequeue()
  {
    Object o;
    synchronized(this)
    {
      while(size == 0)
      {
	try { wait(); }
	catch(InterruptedException ignored) {}
      }
      o = data[first];
      first++;
      size--;
      if(first == data.length) first = 0;
    }
    return o;
  }
}
