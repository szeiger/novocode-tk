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


package com.novocode.tk.io;

import java.io.*;
import java.net.URL;
import java.util.Random;


/**
 * NFile is a more general version of <EM>java.io.File</EM> which provides
 * access to file-like resources. This is the abstract superclass of all
 * NFile classes, <EM>FileNFile</EM> is the NFile equivalent of
 * <EM>java.io.File</EM>.
 *
 * NFile classes use a unified naming scheme which is independent of the
 * implementation or host platform: NFile paths are separated by a slash
 * ('/') character. Two dots ('..') denote the parent hierarchy.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively Mature.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.io.FileNFile
 * @see java.io.File
 */

public abstract class NFile implements Serializable
{
  private static final Random random = new Random(System.currentTimeMillis());


  /**
   * The path and file name of this NFile object.
   */

  protected String path;

  private File tmpFile;
  private int tmpCount;


  /* Dummy constructor *****************************************************/

  protected NFile() {}


  /* NFile handling ********************************************************/


  /**
   * Returns the parent hierarchy of this NFile.
   */

  public abstract NFile getParent();


  /**
   * Returns an NFile below the hierarchy denoted by this NFile.
   *
   * @param name a relative path; may contain '/' characters.
   */

  public abstract NFile getSub(String name);


  /**
   * Return the name (anything after the last '/' characters) of this
   * NFile.
   */

  public String getName()
  {
    int i = path.lastIndexOf('/');
    if(i>=0) return path.substring(i+1);
    else return path;
  }


  /**
   * Return the path (anything before the last '/' characters) of this
   * NFile.
   */

  public String getPath()
  {
    return path;
  }


  /**
   * Return the canonical path of this NFile.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public String getCanonicalPath() throws NFileException
  {
    return path;
  }


  /**
   * Same as getPath().
   *
   * @see #getPath()
   */

  public String toString()
  {
    return getPath();
  }


  /* Properties ************************************************************/

  /**
   * Check if the resource denoted by this NFile exists.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract boolean exists() throws NFileException;


  /**
   * Check if the resource denoted by this NFile has a content
   * (on a "regular" file system: is a file and not a directory or
   * other object). Note that NFiles can be listable and also have
   * a content at the same time.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract boolean hasContent() throws NFileException;


  /**
   * Check if the resource denoted by this NFile is listable
   * (on a "regular" file system: is a directory)
   * Note that NFiles can be listable and also have
   * a content at the same time.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract boolean isListable() throws NFileException;


  /**
   * Return the last modification date of the resource in milliseconds
   * since 1/1/1970 0:00 UTC.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract long lastModified() throws NFileException;


  /**
   * Check if this NFile represents a real file (or other object)
   * in the local filesystem.
   */

  public boolean isLocalFile() { return false; }


  /**
   * Check if this NFile can be represented by a URL.
   */

  public boolean hasURLRepresentation() { return false; }


  /* Modification **********************************************************/

  /**
   * Make this NFile listable (on a "regular" file system: create a directory)
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract void createListable() throws NFileException;


  /**
   * List the contents of a listable NFile.
   *
   * @return a list of names relative to this NFile.
   * @exception NFileException if the method fails for some reason.
   */

  public abstract String[] list() throws NFileException;


  /**
   * List the contents of a listable NFile using the specified filter.
   *
   * @return a list of names relative to this NFile.
   * @exception NFileException if the method fails for some reason.
   */

  public abstract String[] list(FilenameFilter filter) throws NFileException;


  /**
   * Delete this NFile.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract void delete() throws NFileException;


  /**
   * List the contents of a listable NFile.
   *
   * @return a list of NFiles.
   * @exception NFileException if the method fails for some reason.
   */

  public NFile[] listFiles() throws NFileException
  {
    String[] subs = list();
    if(subs == null) return null;
    NFile[] n = new NFile[subs.length];
    for(int i=0; i<subs.length; i++) n[i] = getSub(subs[i]);
    return n;
  }


  /**
   * List the contents of a listable NFile using the specified filter.
   *
   * @return a list of NFiles.
   * @exception NFileException if the method fails for some reason.
   */

  public NFile[] listFiles(FilenameFilter filter) throws NFileException
  {
    String[] subs = list(filter);
    if(subs == null) return null;
    NFile[] n = new NFile[subs.length];
    for(int i=0; i<subs.length; i++) n[i] = getSub(subs[i]);
    return n;
  }


  /**
   * Move this NFile to the location denoted by another NFile. If this
   * NFile is listable, all children are moved as well. The default
   * implementation in class NFile always copies the resource(s) and
   * then deletes the original. Subclasses should provide more
   * efficient implementations for moving resources to a destination
   * which is of the same sub-class of NFile as the source.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public void moveTo(NFile dest) throws NFileException
  {
    try
    {
      copyTo(dest);
    }
    catch(NFileException e)
    {
      dest.delete();
      throw e;
    }
    delete();
  }


  /**
   * Copy the resource to the destination denoted by another NFile
   * object. If the resource has children, they are copied recursively.
   * The default implementation does not copy meta data. Subclasses which
   * support meta data should extend this method to copy the meta data, too.
   *
   * @param dest the destination.
   * @exception NFileException if the method fails for some reason.
   */

  public void copyTo(NFile dest) throws NFileException
  {
    if(isListable())
    {
      dest.createListable();
      String[] subs = list();
      if(subs != null)
	for(int i=0; i<subs.length; i++)
	  getSub(subs[i]).copyTo(dest.getSub(subs[i]));
    }

    if(hasContent())
    {
      try
      {
	InputStream in = getInputStream();
	OutputStream out = dest.getOutputStream();
	new StreamConnector(in, out).copy();
      }
      catch(IOException e) { throw new NFileException(e); }
    }
  }


  /* Contents **************************************************************/

  /**
   * Return the length of the content of this NFile.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract long length() throws NFileException;


  /**
   * Get an InputStream to read the content of this NFile.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract InputStream getInputStream() throws NFileException;


  /**
   * Get an InputStream to write the content of this NFile. An
   * existing content will be deleted.
   *
   * @exception NFileException if the method fails for some reason.
   */

  public abstract OutputStream getOutputStream() throws NFileException;


  /**
   * Get a local File for this NFile. If <EM>isLocalFile()</EM> returns
   * true, the real file is returned, otherwise a copy of the content
   * is created in the local file system.
   * Use <EM>releaseFile()</EM> to delete the temporary file which was
   * obtained by this resource. Calls to obtainFile() and releaseFile()
   * can be nested.
   *
   * @exception NFileException if the method fails for some reason.
   * @see #releaseFile()
   */

  public synchronized File obtainFile() throws NFileException
  {
    if(tmpFile == null)
    {
      File f = new File("/tmp");
      if(!f.isDirectory()) f = new File(".");
      String prefix = "nfile-tmp-"+System.currentTimeMillis()+'-';
      tmpFile = new File(f, prefix+random.nextLong());
      while(tmpFile.exists())
	tmpFile = new File(f, prefix+random.nextLong());
      try
      {
	new StreamConnector(getInputStream(),
			    new FileOutputStream(tmpFile)).copy();
      }
      catch(IOException e)
      {
	tmpFile = null;
	throw new NFileException(e);
      }
    }
    tmpCount++;
    return tmpFile;
  }


  /**
   * Release a file which was created by <EM>obtainFile()</EM>.
   *
   * @exception NFileException if the method fails for some reason.
   * @see #obtainFile()
   */

  public synchronized void releaseFile() throws NFileException
  {
    if(tmpCount > 0)
    {
      tmpCount--;
      if(tmpCount == 0)
      {
	tmpFile.delete();
	tmpFile = null;
      }
    }
  }


  /**
   * Return a URL for this NFile.
   *
   * @exception NFileException if the resource denoted by this NFile
   *            doesn't have a URL representation.
   */

  public URL getURL() throws NFileException
  {
    throw new NFileException("URL representation not supported");
  }


  /* Meta store ************************************************************/

  /**
   * Check if this NFile supports meta data. Meta data is represented as
   * an associative table which maps String keys to String values.
   *
   * <P><STRONG>Example:</STRONG> A meta data key <EM>Content-Type</EM>
   * could be used to denote the MIME type of an NFile's content.
   */

  public boolean hasMetaStore()
  {
    return false;
  }

  /**
   * Look up a meta value.
   *
   * @param name the key.
   * @exception NFileException if the method fails for some reason.
   * @see #hasMetaStore()
   */

  public String getMeta(String name) throws NFileException
  {
    throw new NFileException("Meta store not supported");
  }


  /**
   * Set a meta value.
   *
   * @exception NFileException if the method fails for some reason.
   * @see #hasMetaStore()
   */

  public void setMeta(String name, String value) throws NFileException
  {
    throw new NFileException("Meta store not supported");
  }
}
