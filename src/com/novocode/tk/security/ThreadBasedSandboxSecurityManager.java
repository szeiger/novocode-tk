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


package com.novocode.tk.security;

import java.util.Hashtable;


/**
 * A SecurityManager which delegates security checks to a Sandbox which
 * is associated with the calling Thread or its ThreadGroup.
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.security.Sandbox
 * @see java.lang.Thread
 * @see java.lang.ThreadGroup
 */

public class ThreadBasedSandboxSecurityManager extends SandboxSecurityManager
{
  private Hashtable groups = new Hashtable(), threads = new Hashtable();


  public synchronized void addGroup(ThreadGroup g, Sandbox s)
  {
    if(groups.get(g) != null)
      throw new SecurityException("You may not set a new sandbox for a "+
				  "protected thread group.");
    Thread[] ta = new Thread[g.activeCount()];
    g.enumerate(ta);
    for(int i=0; i<ta.length; i++)
      if(threads.get(ta[i]) != null)
	throw new SecurityException("You may not set a new sandbox for a "+
				    "thread group that contains a "+
				    "protected thread.");
    s.setSecurityManager(this);
    groups.put(g, s);
  }

  public synchronized void removeGroup(ThreadGroup g, Sandbox old)
  {
    Sandbox s = (Sandbox)groups.get(g);
    if(s != null)
    {
      if(s != old)
	throw new SecurityException("Wrong sandbox; can't remove!");
      s.setSecurityManager(null);
      groups.remove(g);
    }
  }

  public synchronized void addThread(Thread t, Sandbox s)
  {
    Sandbox old1 = (Sandbox)threads.get(t);
    Sandbox old2 = (Sandbox)groups.get(t.getThreadGroup());
    if((old1 != null) || (old2 != null))
      throw new SecurityException("You may not set a new sandbox for a "+
				  "protected thread.");
    s.setSecurityManager(this);
    threads.put(t, s);
  }

  public synchronized void removeThread(Thread t, Sandbox old)
  {
    Sandbox s = (Sandbox)threads.get(t);
    if(s != null)
    {
      if(s != old)
	throw new SecurityException("Wrong sandbox; can't remove!");
      s.setSecurityManager(null);
      threads.remove(t);
    }
  }


  protected Sandbox getSandbox()
  {
    Thread t = Thread.currentThread();
    Sandbox s = (Sandbox)groups.get(t.getThreadGroup());
    if(s == null) s = (Sandbox)threads.get(t);
    return s;
  }
}
