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


/**
 * Plugs two streams together, copying from one to the other.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class StreamConnector implements Runnable
{
  private InputStream in;
  private OutputStream out;
  private int len = -1;
  private boolean closeOutput = true;
  private IOException ioe;


  /**
   * Creates a new StreamConnector that copies data from an InputStream to
   * an OutputStream until the end of the InputStream is reached.
   *
   * @param in an InputStream.
   * @param out an OutputStream.
   */

  public StreamConnector(InputStream in, OutputStream out)
  {
    this.in = in;
    this.out = out;
  }


  /**
   * Creates a new StreamConnector that copies data from an InputStream to
   * an OutputStream until the end of the InputStream is reached or the
   * specified number of bytes has been copied.
   *
   * @param in an InputStream.
   * @param out an OutputStream.
   * @param bytes the maximum number of bytes to copy.
   */

  public StreamConnector(InputStream in, OutputStream out, int bytes)
  {
    this(in, out);
    len = bytes;
  }


  /**
   * Close output stream after copying?
   *
   * @param b true if the stream should be closed, otherwise false.
   *          The default setting is "true".
   */

  public void setCloseOutput(boolean b)
  {
    closeOutput = b;
  }


  /**
   * Copies the data.
   * You can either call this method directly or attach the StreamConnector
   * to a Thread and start the thread.
   *
   * @see java.lang.Thread#start
   */

  public void copy() throws IOException
  {
    try
    {
      int num;
      byte[] ch = new byte[1024];
      if(len == -1)
      {
	while((num = in.read(ch,0,1024)) > 0) out.write(ch,0,num);
      }
      else
      {
	while(true)
	{
	  if(len >= 1024)
	  {
	    num = in.read(ch,0,1024);
	    out.write(ch,0,num);
	    if(num != 1024)
	      throw new IOException("Unexpected end of input stream");
	    len -= 1024;
	  }
	  else
	  {
	    num = in.read(ch,0,len);
	    out.write(ch,0,num);
	    if(num != len)
	      throw new IOException("Unexpected end of input stream");
	    break;
	  }
	}
      }
    }
    finally { if(closeOutput) out.close(); }
  }


  /**
   * Copies the data without throwing an exception. This method can be
   * started in a separate thread and calls <CODE>copy()</CODE>.
   *
   * @see #getException
   */

  public void run()
  {
    try
    {
      ioe = null;
      copy();
    }
    catch(IOException e)
    {
      ioe = e;
    }
  }


  /**
   * Returns an IOException thrown in <CODE>run()</CODE>.
   *
   * @return the IOException thrown in the last call to <CODE>run()</CODE>
   *         or <EM>null</EM> if none was thrown.
   *
   * @see #run
   */

  public IOException getException()
  {
    return ioe;
  }
}
