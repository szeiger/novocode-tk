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

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;


/**
 * An optimized InputStreamReader which reads text with ASCII / ISO-8859-1
 * encoding from an InputStream. The reader is <EM>not</EM> buffered.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Intermediate. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class Latin1InputStreamReader extends Reader
{
  private char[] charbuf;
  private byte[] bytebuf;
  private InputStream in;


  public Latin1InputStreamReader(InputStream in)
  {
    super(in);
    this.in = in;
  }


  public int read() throws IOException
  {
    return in.read();
  }


  public int read(char cbuf[], int off, int len) throws IOException
  {
    if(bytebuf == null || bytebuf.length < len) bytebuf = new byte[len];
    int num = in.read(bytebuf, 0, len);
    int i=0, j=off;
    while(i<num) cbuf[j++] = (char)(bytebuf[i++]&0xFF);
    return num;
  }


  public long skip(long n) throws IOException
  {
    return in.skip(n);
  }


  public void close() throws IOException
  {
    in.close();
    charbuf = null;
    bytebuf = null;
  }


  /** Read a line terminated by \n or EOF. \r characters are discarded.
   *
   * @return null if EOF was the only byte that has been read,
   *         otherwise the read line.
   */

  public String readLine() throws IOException
  {
    int length = 0;
    int c;

    synchronized(in)
    {
      if(charbuf == null) charbuf = new char[128];
      while((c = in.read()) != -1)
      {
	if(c == '\n') return String.copyValueOf(charbuf, 0, length);
	else if(c != '\r')
	{
	  if(length == charbuf.length)
	  {
	    char[] newbuf = new char[length * 2];
	    System.arraycopy(charbuf, 0, newbuf, 0, length);
	    charbuf = newbuf;
	  }
	  charbuf[length++] = (char) c;
	}
      }
      if(length == 0) return null;
      else return String.copyValueOf(charbuf, 0, length);
    }
  }
}
