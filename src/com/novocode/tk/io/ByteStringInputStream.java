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

import java.io.InputStream;
import java.io.IOException;
import com.novocode.tk.util.ByteString;


/**
 * An InputStream that sits on top of another InputStream and provides
 * a method for reading a line of (ASCII / ISO-8859-1) text into a
 * ByteString object.
 *
 * <p>ByteStringInputStream is buffered. Use ByteString's readLine()
 * method if you need unbuffered reads.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.util.ByteString
 */

public final class ByteStringInputStream extends InputStream
{
  InputStream in;
  private byte[] data;
  private int offset, length;


  /**
   * Construct a new ByteStringInputStream with the given initial
   * buffer size.
   *
   * @param in the InputStream from which to read
   * @param len the initial buffer size in bytes
   */

  public ByteStringInputStream(InputStream in, int len)
  {
    this.in = in;
    data = new byte[len];
  }


  /**
   * Construct a new ByteStringInputStream with the default initial
   * buffer size of 256 bytes.
   *
   * @param in the InputStream from which to read
   */

  public ByteStringInputStream(InputStream in) { this(in, 256); }


  public int available() throws IOException { return in.available() + length; }


  /**
   * Close the underlying stream.
   */

  public void close() throws IOException { in.close(); }


  private void check() throws IOException
  {
    if(length == 0)
    {
      length = in.read(data, 0, data.length);
      offset = 0;
    }
  }


  public int read() throws IOException
  {
    if(length == 0) return in.read();
    length--;
    return (int)data[offset++];
  }


  public int read(byte[] b, int off, int len) throws IOException
  {
    int num = 0;
    if(length > 0)
    {
      int copylen = len<length? len:length;
      System.arraycopy(data, offset, b, off, copylen);
      offset += copylen;
      length -= copylen;
      num += copylen;
    }
    if(len > 0) num += in.read(b, off, len);
    return num;
  }


  /**
   * Create a view of the next line (terminated by "\n" or "\r\n") in the
   * InputStream in a supplied ByteString.
   * The ByteString's data array is not modified but simply
   * replaced by the stream's buffer array. The data may be modified
   * and is only valid until new data is read from the ByteStringInputStream
   * by any means.
   *
   * @param b an existing ByteString
   * @return the ByteString which was passed in or <I>null</I> if the
   *         end of the stream was already reached.
   * @exception java.io.IOException if a read operation on the underlying
   *            stream fails.
   */

  public ByteString viewLine(ByteString b) throws IOException
  {
    check();
    if(length <= 0) return null;
    b.data = data;
    b.offset = offset;
    int startoffset = 0;
    while(true)
    {
      for(int i=startoffset; i<length-1; i++)
      {
	if(data[offset+i] == (byte)'\n')
	{
	  b.length = i;
	  offset += i+1;
	  length -= i+1;
	  if(b.byteAt(b.length-1) == (byte)'\r') b.length--;
	  return b;
	}
      }
      if(length+offset<data.length)
      {
	int numread = in.read(data, offset+length, data.length-length-offset);
	if(numread <= 0)
	{
	  if(length == 0) return null;
	  b.length = length-1;
	  if(length == 1 && data[offset] == (byte)'\n') b.length = 0;
	  length = 0;
	  if(b.length > 0 && b.byteAt(b.length-1) == (byte)'\r') b.length--;
	  return b;
	}
	startoffset = length-1;
	length += numread;
      }
      else if(offset*2 > data.length)
      {
	System.arraycopy(data, offset, data, 0, length);
	b.offset = offset = 0;
	startoffset = length-1;
	int numread = in.read(data, length, data.length-length);
	if(numread > 0) length += numread;
      }
      else
      {
	byte[] newdata = new byte[data.length * 2];
	System.arraycopy(data, offset, newdata, 0, length);
	data = newdata;
	b.data = data;
	b.offset = offset = 0;
	int numread = in.read(data, length, data.length-length);
	if(numread <= 0)
	{
	  if(length == 0) return null;
	  b.length = length-1;
	  if(length == 1 && data[offset] == (byte)'\n') b.length = 0;
	  length = 0;
	  if(b.length > 0 && b.byteAt(b.length-1) == (byte)'\r') b.length--;
	  return b;
	}
	startoffset = length-1;
	length += numread;
      }
    }
  }


  //public long skip(long n) throws IOException { }
}
