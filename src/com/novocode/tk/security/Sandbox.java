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
 * A lightweight security manager interface which contains the same
 * security check methods as java.lang.SecurityManager. Sandboxes are
 * used by the SandboxSecurityManager.
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.security.SandboxSecurityManager
 * @see java.lang.SecurityManager
 */

public interface Sandbox
{
  public abstract void checkCreateClassLoader();
  public abstract void checkAccess(Thread g);
  public abstract void checkAccess(ThreadGroup g);
  public abstract void checkExit(int status);
  public abstract void checkExec(String cmd);
  public abstract void checkLink(String lib);
  public abstract void checkRead(FileDescriptor fd);
  public abstract void checkRead(String file);
  public abstract void checkRead(String file, Object context);
  public abstract void checkWrite(FileDescriptor fd);
  public abstract void checkWrite(String file);
  public abstract void checkDelete(String file);
  public abstract void checkConnect(String host, int port);
  public abstract void checkConnect(String host, int port, Object context);
  public abstract void checkListen(int port);
  public abstract void checkAccept(String host, int port);
  public abstract void checkMulticast(InetAddress maddr);
  public abstract void checkMulticast(InetAddress maddr, byte ttl);
  public abstract void checkPropertiesAccess();
  public abstract void checkPropertyAccess(String key);
  public abstract boolean checkTopLevelWindow(Object window);
  public abstract void checkPrintJobAccess();
  public abstract void checkSystemClipboardAccess();
  public abstract void checkAwtEventQueueAccess();
  public abstract void checkPackageAccess(String pkg);
  public abstract void checkPackageDefinition(String pkg);
  public abstract void checkSetFactory();
  public abstract void checkMemberAccess(Class clazz, int which);
  public abstract void checkSecurityAccess(String action);
  public abstract ThreadGroup getThreadGroup();
  public abstract void setSecurityManager(SecurityManager man);
}
