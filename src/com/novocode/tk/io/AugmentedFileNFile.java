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
import java.util.Properties;


/**
 * AugmentedFileNFile extends FileNFile to add meta data support.
 *
 * Meta data for a file "<CODE>file</CODE>" is stored in the properties
 * file "<CODE>.nfile-meta-<I>&lt;file&gt;</I></CODE>" in the same directory.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.io.NFile
 * @see com.novocode.tk.io.FileNFile
 * @see java.io.File
 */

public class AugmentedFileNFile extends FileNFile
{
  protected static final Object metaLock = new Object();

  protected File mf;


  public AugmentedFileNFile(String name)
  {
    super(name);
    findMeta();
  }


  public AugmentedFileNFile(File file)
  {
    super(file);
    findMeta();
  }


  private void findMeta()
  {
    mf = new File(f.getParent(), ".nfile-meta-" + f.getName());
  }


  public NFile getParent()
  {
    String s = f.getParent();
    if(f == null) return null;
    else return new AugmentedFileNFile(new File(s));
  }


  public NFile getSub(String name)
  {
    return
      new AugmentedFileNFile(new File(f,
				      name.replace('/', File.separatorChar)));
  }


  public void delete() throws NFileException
  {
    super.delete();
    deleteMeta();
  }


  private void deleteMeta() throws NFileException
  {
    mf.delete();
    if(mf.exists())
    {
      File dest =
	new File(mf.getParent(), ".delete-me-"+System.currentTimeMillis());
      mf.renameTo(dest);
      if(mf.exists())
	throw new NFileException("Failed to delete meta file "+mf);
    }
  }


  public boolean hasMetaStore()
  {
    return true;
  }


  public String getMeta(String name) throws NFileException
  {
    synchronized(metaLock)
    {
      try
      {
	Properties props = loadMeta();
	return props.getProperty(name.toLowerCase());
      }
      catch(IOException e)
      {
	throw new NFileException(e);
      }
    }
  }


  public void setMeta(String name, String value) throws NFileException
  {
    synchronized(metaLock)
    {
      try
      {
	Properties props = loadMeta();
	props.put(name.toLowerCase(), value);
	FileOutputStream fout = new FileOutputStream(mf);
	props.save(fout, "NFile meta data");
	fout.close();
      }
      catch(IOException e)
      {
	throw new NFileException(e);
      }
    }
  }


  private Properties loadMeta() throws IOException
  {
    Properties props = new Properties();
    if(mf.exists())
    {
      FileInputStream fin = new FileInputStream(mf);
      props.load(fin);
      fin.close();
    }
    return props;
  }


  public void finalize() throws Throwable
  {
    if(!exists()) deleteMeta();
    super.finalize();
  }
}
