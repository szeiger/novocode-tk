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
 * A no-op sandbox which allows everything.
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.security.Sandbox
 * @see com.novocode.tk.security.SandboxSecurityManager
 */

public class FullyTrustedSandbox extends SandboxImpl
{
  public void checkCreateClassLoader() {}
  public void checkAccess(Thread g) {}
  public void checkAccess(ThreadGroup g) {}
  public void checkExit(int status) {}
  public void checkExec(String cmd) {}
  public void checkLink(String lib) {}
  public void checkRead(FileDescriptor fd) {}
  public void checkRead(String file) {}
  public void checkRead(String file, Object context) {}
  public void checkWrite(FileDescriptor fd) {}
  public void checkWrite(String file) {}
  public void checkDelete(String file) {}
  public void checkConnect(String host, int port) {}
  public void checkConnect(String host, int port, Object context) {}
  public void checkListen(int port) {}
  public void checkAccept(String host, int port) {}
  public void checkMulticast(InetAddress maddr) {}
  public void checkMulticast(InetAddress maddr, byte ttl) {}
  public void checkPropertiesAccess() {}
  public void checkPropertyAccess(String key) {}
  public boolean checkTopLevelWindow(Object window) { return true; }
  public void checkPrintJobAccess() {}
  public void checkSystemClipboardAccess() {}
  public void checkAwtEventQueueAccess() {}
  public void checkPackageAccess(String pkg) {}
  public void checkPackageDefinition(String pkg) {}
  public void checkSetFactory() {}
  public void checkMemberAccess(Class clazz, int which) {}
  public void checkSecurityAccess(String action) {}
}
