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

import java.util.Hashtable;
import java.io.File;
import java.io.FileInputStream;


/**
 * A ClassLoader which loads bytecode from *.class files in a specified
 * directory.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.util.FileClassLoader
 */

public class DirClassLoader extends ClassLoader
{
  private Hashtable cache = new Hashtable();
  private String dir;

  /**
   * Creates a new DirClassLoader for the given directory.
   *
   * @param dir the directory from which the classes are to be loaded.
   */

  public DirClassLoader(String dir) { this.dir = dir; }


  private byte[] loadClassData(String  name) throws ClassNotFoundException
  {
    File file = new File(dir + File.separatorChar + name + ".class");
    if(!file.exists())
      throw new ClassNotFoundException("File " + file + " not found");
    try
    {
      int l = (int)file.length();
      FileInputStream f = new FileInputStream(file);
      byte[] b = new byte[l];
      f.read(b);
      return b;
    }
    catch(Exception e) { throw new ClassNotFoundException(e.toString()); }
  }


  /**
   * Loads a class.
   * If a system class with the given name exists, it is returned. Otherwise
   * the class is loaded from the DirClassLoader's directory.
   *
   * @param name the name of the class to be loaded (without ".class").
   * @param resolve set this to true if instances of the class are to be
   *                created.
   * @return the requested class.
   * @exception ClassNotFoundException if the class is not a system class
   *                                   and can't be loaded from the
   *                                   directory.
   */

  public synchronized Class loadClass(String  name, boolean resolve)
                      throws ClassNotFoundException
  {
    Class c = (Class)cache.get(name);
    if(c == null)
    {
      try { c = findSystemClass(name); }
      catch(ClassNotFoundException e)
      {
	byte[] data = loadClassData(name);
	c = defineClass(name, data, 0, data.length);
	cache.put(name, c);
      }
    }
    if(resolve) resolveClass(c);
    return  c;
  }
}
