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


package com.novocode.tk.mime;

import java.io.InputStream;
import java.io.IOException;


/**
 * An InputStream for reading MIME multipart bodies.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Immature. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class BoundaryInputStream extends InputStream
{
  private InputStream in;
  private byte[] data, bound;
  private int start, end, boundary, mark = -1;
  private String boundString;


  /**
   * Creates a new BoundaryInputStream.
   *
   * @param in the InputStream from which this streams is reading
   * @param length the number of bytes to read
   */

  public BoundaryInputStream(InputStream in, int length) throws IOException
  {
    this.in = in;
    data = new byte[length];
    end = in.read(data, start, length);
    if(end != length) throw new IOException("Unexpected end of input stream");
    boundary = end;
  }


  /**
   * Sets the boundary which separates the parts of the body.
   */

  public String setBoundary(String boundaryString)
  {
    String oldBoundString = boundString;
    boundString = boundaryString;
    if(boundaryString == null) boundary = end;
    else
    {
      bound = new byte[boundaryString.length()+4];
      boundaryString.getBytes(0, boundaryString.length(), bound, 4);
      bound[0] = (byte)'\r';
      bound[1] = (byte)'\n';
      bound[2] = bound[3] = (byte)'-';
    }
    return oldBoundString;
  }


  /**
   * Skips the Preamble of the multipart body and advances to the first
   * part of the body.
   */

  public boolean skipPreamble()
  {
    int max = end-bound.length-2;
  test:
    for(int i=start; i<=max; i++)
    {
      int n = bound.length-2, j = i, k = 2;
      while(n-- != 0) if(data[j++] != bound[k++]) continue test;
      boundary = i - 2;
      return nextPart();
    }
    return false;
  }


  /**
   * Advances to the next part of the body. If the current part has not
   * been fully read, the rest of it is automatically skipped.
   *
   * @return true if the body has another part; otherwise false
   */

  public boolean nextPart()
  {
    start = lineAfter(boundary+2);
    if(data[start-1] == (byte)'-' && data[start-2] == (byte)'-') return false;
    int max = end-bound.length;
  test:
    for(int i=start; i<=max; i++)
    {
      int n = bound.length, j = i, k = 0;
      while(n-- != 0) if(data[j++] != bound[k++]) continue test;
      boundary = i;
      return true;
    }
    return false;
  }


  private int lineAfter(int i)
  {
    while(i < end) if(data[i++] == (byte)'\n') return i;
    return end;
  }


  /**
   * Returns the number of bytes left in the current part of the body.
   */

  public int available() throws IOException { return boundary - start; }


  /**
   * Closes this stream and the underlying InputStream.
   *
   * @exception java.io.IOException if closing the underlying InputStream
   *            throws such an exception.
   */

  public void close() throws IOException { in.close(); }


  /**
   * Marks the current position in the stream.
   * You can return to it later by calling <EM>reset()</EM>.
   *
   * @see #reset
   */

  public void mark(int readlimit) { mark = start; }


  /**
   * @return true
   */

  public boolean markSupported() { return true; }


  /**
   * Reads a single byte from the current body part.
   *
   * @return the read byte as an int or -1 if the end of the part has
   *         been reached
   */

  public int read() throws IOException
  {
    if(start < boundary) return data[start++] & 0xFF; else return -1;
  }


  /**
   * Reads an array of bytes from the current body part.
   */

  public int read(byte[] b, int off, int len) throws IOException
  {
    if(start == boundary) return 0;
    if(len > boundary-start) len = boundary-start;
    System.arraycopy(data, start, b, off, len);
    start += len;
    return len;
  }


  /**
   * Moves back in the stream to the marked position.
   * You may not move back to a mark in a previous part.
   *
   * @see #mark
   */

  public void reset() throws IOException
  {
    if(mark == -1) throw new IOException("Mark not set");
    start = mark;
  }


  /**
   * Skips the specified number of bytes in the current part.
   * No more data is skipped when the end of the part has been reached.
   */

  public long skip(long n) throws IOException
  {
    if(n > boundary-start) n = boundary-start;
    start += (int)n;
    return n;
  }
}
