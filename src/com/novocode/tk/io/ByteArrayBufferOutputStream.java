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
 * ByteArrayBufferOutputStream is a subclass of java.io.ByteArrayOutputStream
 * which provides accessor methods for the protected <EM>buf</EM> and
 * <EM>count</EM> variables.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */


public final class ByteArrayBufferOutputStream extends ByteArrayOutputStream
{
  /**
   * Returns an InputStream which provides access to the data which is
   * in the buffer at the time this method is called.
   */

  public InputStream getInputStream()
  {
    return new ByteArrayInputStream(buf, 0, count);
  }


  /**
   * Returns the current buffer.
   */

  public byte[] getBuffer() { return buf; }


  /**
   * Returns the number of bytes in the buffer.
   */

  public int getCount() { return count; }
}
