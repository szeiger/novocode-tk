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
import java.net.MalformedURLException;


/**
 * FileNFile is an NFile implementation which uses local files which are
 * accessed through <EM>java.io.File</EM> objects.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively Mature.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.io.NFile
 * @see com.novocode.tk.io.AugmentedFileNFile
 * @see java.io.File
 */

public class FileNFile extends NFile
{
  protected File f;


  public FileNFile(String name)
  {
    path = name;
    f = new File(name.replace('/', File.separatorChar));
  }


  public FileNFile(File file)
  {
    f = file;
    path = f.getPath().replace(File.separatorChar, '/');
  }


  public String getCanonicalPath() throws NFileException
  {
    try
    {
      return f.getCanonicalPath().replace(File.separatorChar, '/');
    }
    catch(IOException e) { throw NFileException.getCascadeException(e); }
  }


  public NFile getParent()
  {
    String s = f.getParent();
    if(f == null) return null;
    else return new FileNFile(new File(s));
  }


  public NFile getSub(String name)
  {
    return new FileNFile(new File(f, name.replace('/', File.separatorChar)));
  }


  public boolean exists() throws NFileException
  {
    return f.exists();
  }


  public boolean hasContent() throws NFileException
  {
    return f.isFile();
  }


  public boolean isListable() throws NFileException
  {
    return f.isDirectory();
  }


  public boolean isLocalFile()
  {
    return true;
  }


  public long lastModified() throws NFileException
  {
    return f.lastModified();
  }


  public void createListable() throws NFileException
  {
    f.mkdirs();
    if(!f.isDirectory())
      throw new NFileException("Failed to create directories");
  }


  public void moveTo(NFile dest) throws NFileException
  {
    if(!(dest instanceof FileNFile)) super.moveTo(dest);
    else if(!f.renameTo(((FileNFile)dest).f))
      throw new NFileException("Failed to move "+f+" to "+((FileNFile)dest).f);
  }


  public String[] list() throws NFileException
  {
    return f.list();
  }


  public String[] list(FilenameFilter filter) throws NFileException
  {
    return f.list(filter);
  }


  public void delete() throws NFileException
  {
    if(!f.delete())
      throw new NFileException("Failed to delete file "+f);
  }


  public long length() throws NFileException
  {
    return f.length();
  }


  public InputStream getInputStream() throws NFileException
  {
    try
    {
      return new FileInputStream(f);
    }
    catch(IOException e) { throw NFileException.getCascadeException(e); }
  }


  public OutputStream getOutputStream() throws NFileException
  {
    try
    {
      return new FileOutputStream(f);
    }
    catch(IOException e) { throw NFileException.getCascadeException(e); }
  }


  public File obtainFile() throws NFileException
  {
    return f;
  }


  public void releaseFile() throws NFileException
  {
  }


  public boolean hasURLRepresentation()
  {
    return true;
  }


  public URL getURL() throws NFileException
  {
    try
    {
      return new URL("file:"+f.getAbsolutePath());
    }
    catch(MalformedURLException e) { throw new NFileException(e); }
  }
}
