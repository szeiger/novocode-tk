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

import java.io.FileDescriptor;
import java.net.InetAddress;


/**
 * The abstract superclass of all sandbox-based security managers.
 * Subclasses need to implement the getSandbox() method.
 *
 * @author Stefan Zeiger
 * @see java.lang.SecurityManager
 * @see com.novocode.tk.security.Sandbox
 */

public abstract class SandboxSecurityManager extends SecurityManager
{
  protected abstract Sandbox getSandbox();


  public void checkCreateClassLoader()
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkCreateClassLoader();
  }

  public void checkAccess(Thread g)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkAccess(g);
  }

  public void checkAccess(ThreadGroup g)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkAccess(g);
  }

  public void checkExit(int status)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkExit(status);
  }

  public void checkExec(String cmd)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkExec(cmd);
  }

  public void checkLink(String lib)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkLink(lib);
  }

  public void checkRead(FileDescriptor fd)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkRead(fd);
  }

  public void checkRead(String file)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkRead(file);
  }

  public void checkRead(String file, Object context)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkRead(file, context);
  }

  public void checkWrite(FileDescriptor fd)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkWrite(fd);
  }

  public void checkWrite(String file)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkWrite(file);
  }

  public void checkDelete(String file)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkDelete(file);
  }

  public void checkConnect(String host, int port)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkConnect(host, port);
  }

  public void checkConnect(String host, int port, Object context)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkConnect(host, port, context);
  }

  public void checkListen(int port)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkListen(port);
  }

  public void checkAccept(String host, int port)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkAccept(host, port);
  }

  public void checkMulticast(InetAddress maddr)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkMulticast(maddr);
  }

  public void checkMulticast(InetAddress maddr, byte ttl)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkMulticast(maddr, ttl);
  }

  public void checkPropertiesAccess()
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkPropertiesAccess();
  }

  public void checkPropertyAccess(String key)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkPropertyAccess(key);
  }

  public boolean checkTopLevelWindow(Object window)
  {
    Sandbox s = getSandbox();
    if(s != null) return s.checkTopLevelWindow(window);
    else return true;
  }

  public void checkPrintJobAccess()
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkPrintJobAccess();
  }

  public void checkSystemClipboardAccess()
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkSystemClipboardAccess();
  }

  public void checkAwtEventQueueAccess()
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkAwtEventQueueAccess();
  }

  public void checkPackageAccess(String pkg)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkPackageAccess(pkg);
  }

  public void checkPackageDefinition(String pkg)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkPackageDefinition(pkg);
  }

  public void checkSetFactory()
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkSetFactory();
  }

  public void checkMemberAccess(Class clazz, int which)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkMemberAccess(clazz, which);
  }

  public void checkSecurityAccess(String action)
  {
    Sandbox s = getSandbox();
    if(s != null) s.checkSecurityAccess(action);
  }

  public ThreadGroup getThreadGroup()
  {
    Sandbox s = getSandbox();
    if(s != null) return s.getThreadGroup();
    else return Thread.currentThread().getThreadGroup();
  }
}
