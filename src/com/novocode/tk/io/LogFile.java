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

import java.io.RandomAccessFile;
import java.io.IOException;


/**
 * A meta-file to which lines of text can be appended.
 * The real file managed by a LogFile object can be changed transparently.
 * If the old file was opened for writing it is automatically closed. The new
 * file is not opened until a message is to be logged.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class LogFile
{
  private RandomAccessFile raf;
  private String name, openedName;
  private boolean changed;

  private static String eol = System.getProperty("line.separator");


  /**
   * Creates a new LogFile without an underlying file.
   * A file has to be specified before writing to the LogFile.
   *
   * @see #setName
   */

  public LogFile() {}


  /**
   * Creates a new LogFile that writes to the specified file.
   *
   * @param fileName the name of the file to write to.
   */

  public LogFile(String fileName)
  {
    name = fileName;
    changed = true;
  }


  /**
   * Select a new file to write to.
   *
   * @param fileName the name of the file to write to.
   */

  public synchronized void setName(String fileName)
  {
    name = fileName;
    changed = true;
  }


  /**
   * Close the underlying file.
   * If no file was opened this method does nothing.
   */

  public synchronized void close()
  {
    if(raf != null)
    {
      try { raf.close(); } catch(IOException ignored) {}
      raf = null;
    }
  }


  /**
   * Write a line of text to the file.
   * The specified text, followed by the host system's line separator, is
   * written to the underlying file.
   *
   * @param data a line of text (without trailing line separator).
   * @exception java.io.IOException if the message could not be written.
   */

  public synchronized void println(String data) throws IOException
  {
    if(!checkRaf())
      throw new IOException("There's no underlying file to write to.");
    raf.writeBytes(data);
    raf.writeBytes(eol);
  }


  /**
   * Get the name of the file to which messages are to be written.
   *
   * @return the name of the file or null if no file was set.
   */

  public String getName() { return name; }


  /**
   * Get the name of the file which is currently opened for writing.
   *
   * @return the name of the file or null if no file is currently opened.
   */

  public String getOpenedName() { return openedName; }


  private boolean checkRaf()
  {
    if(changed)
    {
      if((raf == null) || (!(name.equals(openedName))))
      {
	close();
	if(name != null)
	{
	  try
	  {
	    raf = new RandomAccessFile(name, "rw");
	    openedName = name;
	    raf.seek(raf.length());
	  }
	  catch(IOException e) { close(); }
	}
      }
    }

    if(raf == null) return false; else return true;
  }


  public void finalize() { close(); }
}
