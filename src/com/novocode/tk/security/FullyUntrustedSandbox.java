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
 * A very restrictive sandbox which allows nothing.
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.security.Sandbox
 * @see com.novocode.tk.security.SandboxSecurityManager
 */

public class FullyUntrustedSandbox extends SandboxImpl
{
  public void checkCreateClassLoader()
  {
    throw new SecurityException();
  }

  public void checkAccess(Thread g)
  {
    throw new SecurityException();
  }

  public void checkAccess(ThreadGroup g)
  {
    throw new SecurityException();
  }

  public void checkExit(int status)
  {
    throw new SecurityException();
  }

  public void checkExec(String cmd)
  {
    throw new SecurityException();
  }

  public void checkLink(String lib)
  {
    throw new SecurityException();
  }

  public void checkRead(FileDescriptor fd)
  {
    throw new SecurityException();
  }

  public void checkRead(String file)
  {
    throw new SecurityException();
  }

  public void checkRead(String file, Object context)
  {
    throw new SecurityException();
  }

  public void checkWrite(FileDescriptor fd)
  {
    throw new SecurityException();
  }

  public void checkWrite(String file)
  {
    throw new SecurityException();
  }

  public void checkDelete(String file)
  {
    throw new SecurityException();
  }

  public void checkConnect(String host, int port)
  {
    throw new SecurityException();
  }

  public void checkConnect(String host, int port, Object context)
  {
    throw new SecurityException();
  }

  public void checkListen(int port)
  {
    throw new SecurityException();
  }

  public void checkAccept(String host, int port)
  {
    throw new SecurityException();
  }

  public void checkMulticast(InetAddress maddr)
  {
    throw new SecurityException();
  }

  public void checkMulticast(InetAddress maddr, byte ttl)
  {
    throw new SecurityException();
  }

  public void checkPropertiesAccess()
  {
    throw new SecurityException();
  }

  public void checkPropertyAccess(String key)
  {
    throw new SecurityException();
  }

  public boolean checkTopLevelWindow(Object window)
  {
    throw new SecurityException();
  }

  public void checkPrintJobAccess()
  {
    throw new SecurityException();
  }

  public void checkSystemClipboardAccess()
  {
    throw new SecurityException();
  }

  public void checkAwtEventQueueAccess()
  {
    throw new SecurityException();
  }

  public void checkPackageAccess(String pkg)
  {
    throw new SecurityException();
  }

  public void checkPackageDefinition(String pkg)
  {
    throw new SecurityException();
  }

  public void checkSetFactory()
  {
    throw new SecurityException();
  }

  public void checkMemberAccess(Class clazz, int which)
  {
    throw new SecurityException();
  }

  public void checkSecurityAccess(String action)
  {
    throw new SecurityException();
  }
}
