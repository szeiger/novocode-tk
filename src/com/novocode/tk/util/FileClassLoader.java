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
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;


/**
 * A ClassLoader which loads a single class from a file.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.util.DirClassLoader
 */

public class FileClassLoader extends ClassLoader
{
  private File file;
  private long lastModified;
  private Class loadedClass;
  private Hashtable cache;
  private String dir;


  /**
   * Creates a new FileClassLoader with another FileClassLoader's
   * parameters.
   * This constructor is used to create a new FileClassLoader for a
   * class which has changed on disk and needs to be reloaded.
   *
   * @param loader a FileClassLoader.
   */

  public FileClassLoader(FileClassLoader loader)
  {
    this(loader.file, loader.cache != null);
  }


  /**
   * Creates a new FileClassLoader for the given File.
   *
   * @param file a File.
   * @param resolve true if referenced classed should be searched in the
   *        main class file's directory.
   */

  public FileClassLoader(File file, boolean resolve)
  {
    this.file = file;
    if(resolve)
    {
      cache = new Hashtable();
      dir = file.getParent();
    }
  }


  /**
   * Creates a new FileClassLoader for the given File. Referenced classes
   * are not searched in the main class file's directory.
   *
   * @param file a File.
   */

  public FileClassLoader(File file) { this(file, false); }


  /**
   * Checks whether the class which is managed by this loader has changed
   * on disk and needs to be reloaded.
   *
   * @return true if the class file exists, has already been loaded, and
   *         the version on disk is newer than the loaded version;
   *         otherwise false.
   */

  public boolean hasChanged()
  {
    if((!file.exists()) || (lastModified == 0)) return false;
    Enumeration e = cache.elements();
    while(e.hasMoreElements())
      if(((CacheEntry)e.nextElement()).hasChanged()) return true;
    return file.lastModified() > lastModified;
  }


  private byte[] loadClassData(File f) throws ClassNotFoundException
  {
    if(!f.exists()) throw new ClassNotFoundException("File "+f+" not found");
    try
    {
      int l = (int)f.length();
      FileInputStream in = new FileInputStream(f);
      byte[] b = new byte[l];
      in.read(b);
      return b;
    }
    catch(Exception e) { throw new ClassNotFoundException(e.toString()); }
  }


  /**
   * Returns the class which is managed by this loader, loading it from disk
   * if necessary.
   *
   * @return the requested class, resolved and ready to be instantiated.
   * @exception ClassNotFoundException if the class could not be loaded from
   *                                   disk.
   */

  public Class loadClass() throws ClassNotFoundException
  {
    if(loadedClass != null) return loadedClass;

    lastModified = file.lastModified();
    byte[] b = loadClassData(file);
    Class c = defineClass(null, b, 0, b.length);
    loadedClass = c;
    resolveClass(c);
    return c;
  }


  /**
   * Returns the class managed by this loader or a system class.
   * This method should not be called by user code. Use loadClass() instead.
   * This method is called by the JVM for resolving classes which
   * are referenced by the managed class.
   *
   * @param name the name of the class to be loaded.
   * @param resolve set this to true if instances of the class are to be
   *                created.
   * @return the requested class.
   * @exception ClassNotFoundException if the class was not found.
   * @see #loadClass()
   */

  public synchronized Class loadClass(String  name, boolean resolve)
                      throws ClassNotFoundException
  {
    Class c = null;

    if(cache != null)
    {
      CacheEntry entry = (CacheEntry)cache.get(name);
      if(entry != null) c = entry.clazz;
    }

    if(c == null)
    {
      try { c = findSystemClass(name); }
      catch(ClassNotFoundException e)
      {
	if(loadedClass != null && name.equals(loadedClass.getName()))
	  return loadedClass;
	else if(cache != null)
	{
	  File entryFile = new File(dir, name + ".class");
	  CacheEntry entry = new CacheEntry(entryFile);
	  byte[] data = loadClassData(entryFile);
	  c = defineClass(name, data, 0, data.length);
	  entry.clazz = c;
	  cache.put(name, entry);
	}
	else throw e;
      }
    }

    if(resolve) resolveClass(c);
    return  c;
  }


  /**
   * @return a File object representing the managed class.
   */

  public File getFile() { return file; }


  private static final class CacheEntry
  {
    Class clazz;
    private File file;
    private long lastModified;

    CacheEntry(File file)
    {
      this.file = file;
      this.lastModified = file.lastModified();
    }

    boolean hasChanged() { return file.lastModified() > lastModified; }
  }
}
