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

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A replacement for java.lang.String and java.lang.StringBuffer which
 * uses bytes instead of chars and is therefore well suited
 * for byte-based client/server protocols. ByteStrings can be used
 * like immutable Strings but they also have methods that modify their
 * contents. For reasons of efficiency all methods are unsynchronized, so
 * don't access a ByteString from several threads if you use methods
 * that modify it.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.util.ConstByteString
 * @see java.lang.String
 * @see java.lang.StringBuffer
 */

public final class ByteString extends ConstByteString
{
  private static final int MIN = 32;
  private static final byte[] CRLF = { (byte)'\r', (byte)'\n' };


  /** Creates an empty ByteString */

  public ByteString() {}


  /** Creates a new ByteString with a buffer of the specified length */

  public ByteString(int length)
  {
    if(length > 0) data = new byte[length>MIN?length:MIN];
  }


  /** Creates a new ByteString which is a copy of a String. */

  public ByteString(String str) { copyOf(str); }


  /** Creates a new ByteString which is a copy of a StringBuffer. */

  public ByteString(StringBuffer b) { copyOf(b); }


  /** Checks if the current data array is big enough for the current
   * length and enlarges it if necessary. It also sets the offset to 0. This
   * method may destroy the old data and should thus only be used before
   * filling the string with new data.
   *
   * @see #ensureCapacity
   */

  public final void checkCapacity()
  {
    if(data == null || data.length < length)
      data = new byte[length>MIN?length:MIN];
    offset = 0;
  }


  /** Checks if the current data array is big enough for the specified
   * capacity and enlarges it if necessary. It also sets the offset to 0. This
   * method may destroy the old data and should thus only be used before
   * filling the string with new data.
   *
   * @see #ensureCapacity
   */

  public final void checkCapacity(int capacity)
  {
    if(data == null || data.length < capacity)
      data = new byte[capacity>MIN?capacity:MIN];
    offset = 0;
  }


  /** Checks if the current data array is big enough for the specified
   * capacity and enlarges it if necessary by a factor of 2, keeping the
   * old data.
   *
   * @see #checkCapacity
   */

  public final void ensureCapacity(int capacity)
  {
    int newcap;

    //if(length == 0) newcap = capacity>MIN?capacity:MIN;
    //else { newcap = length; while(newcap < capacity) newcap *= 2; }
    newcap = capacity>MIN?capacity:MIN;

    if(data == null) data = new byte[newcap];
    else if(data.length-offset < newcap)
    {
      if(data.length < newcap)
      {
	byte[] newdata = new byte[newcap];
	System.arraycopy(data, offset, newdata, 0, length);
	data = newdata;
      }
      else System.arraycopy(data, offset, data, 0, length);
      offset = 0;
    }
  }


  /** Compares this string lexicographically to another one.
   *
   * @return 0 if the strings are identical, a value less than 0
   *         if this string is lexicographically less than
   *         the other one, a value greater than 0 if this string
   *         is lexicographically greater than the other one.
   */

  public final int compareTo(ConstByteString other)
  {
    int i;
    for(i=0; i<length && i<other.length; i++)
    {
      int diff = (data[offset+i]&0xFF) - (other.data[other.offset+i]&0xFF);
      if(diff != 0) return diff;
    }
    if(i<other.length) return -1;
    if(i<length) return 1;
    return 0;
  }


  /**
   * @return a new ByteString which is a concatenation of this string and
   *         the other string.
   */

  public final ByteString concat(ConstByteString other)
  {
    ByteString n = new ByteString(length+other.length);
    if(length > 0) System.arraycopy(data, offset, n.data, 0, length);
    if(other.length > 0)
      System.arraycopy(other.data, other.offset, n.data, length, other.length);
    return n;
  }


  /** Fills this string with the contents of a <i>String</i>. The upper
   * 8 bits of each of the <i>String</i>'s characters are discarded.
   *
   * @param s a String
   * @return this string, modified.
   */

  public final ByteString copyOf(String s)
  {
    if((length = s.length()) == 0) return this;
    checkCapacity();
    s.getBytes(0, length, data, 0);
    return this;
  }


  /** Attaches the contents of a <i>String</i>, using the specified encoding,
   * to this <i>ByteString</i>. The existing array is detached and not
   * modified.
   *
   * @param s a String
   * @param enc a character encoding
   * @return this string, with new data.
   */

  public final ByteString copyOf(String s, String enc)
         throws UnsupportedEncodingException
  {
    data = s.getBytes(enc);
    offset = 0;
    length = data.length;
    return this;
  }


  /** Fills this string with the contents of a <i>StringBuffer</i>. The upper
   * 8 bits of each of the <i>StringBuffer</i>'s characters are discarded.
   *
   * @return this string, modified.
   */

  public final ByteString copyOf(StringBuffer b)
  {
    synchronized(b)
    {
      if((length = b.length()) == 0) return this;
      checkCapacity();
      for(int i=0; i<length; i++) data[i] = (byte)b.charAt(i);
    }
    return this;
  }


  /** Fills this string with a copy of another string.
   *
   * @return this string, modified.
   */

  public final ByteString copyOf(ConstByteString other)
  {
    length = other.length;
    checkCapacity();
    System.arraycopy(other.data, other.offset, data, 0, length);
    return this;
  }


  /** Makes this string a view of another string.
   *
   * @return this string, modified.
   */

  public final ByteString viewOf(ByteString other)
  {
    data = other.data;
    offset = other.offset;
    length = other.length;
    return this;
  }


  /** Fills this string with the string representation of an <i>int</i> value.
   *
   * @return this string, modified.
   */

  public final ByteString valueOf(int i) { return valueOf((long)i); }


  /** Fills this string with the string representation of a <i>long</i> value.
   *
   * @return this string, modified.
   */

  public final ByteString valueOf(long l)
  {
    if(l == 0) { length = 1; checkCapacity(); data[0] = (byte)'0'; }
    else
    {
      length = 1;
      boolean sign;
      if(l<0) { l = -l; sign = true; length++; } else sign = false;
      long m = l;
      while((m/=10) > 0) length++;
      checkCapacity();
      if(sign) data[0] = (byte)'-';
      for(int i=length-1; l>0; i--, l/=10) data[i] = (byte)((l % 10) + '0');
    }
    return this;
  }


  /** Appends another ByteString to the end of this one.
   *
   * @return this string, modified.
   */

  public final ByteString append(ConstByteString other)
  {
    int l = length * 2, m = length + other.length;
    if(offset+m > data.length) ensureCapacity(l>m?l:m);
    System.arraycopy(other.data, other.offset,
		     data, offset+length, other.length);
    length += other.length;
    return this;
  }


  /** Appends a <i>byte</i> to the end of this string.
   *
   * @return this string, modified.
   */

  public final ByteString append(byte b)
  {
    if(offset+length == data.length) ensureCapacity(length * 2);
    data[offset+length] = b;
    length++;
    return this;
  }


  /** Appends a <i>String</i> to the end of this ByteString. The upper eight
   * bits of the String's character data are discarded.
   *
   * @return this string, modified.
   */

  public final ByteString append(String s)
  {
    int sl = s.length(), l = length * 2, m = length + sl;
    if(offset+m > data.length) ensureCapacity(l>m?l:m);
    s.getBytes(0, sl, data, offset+length);
    length += sl;
    return this;
  }


  /** Appends a <i>long</i> to the end of this ByteString.
   *
   * @return this string, modified.
   */

  public final ByteString append(long l)
  {
    if(l == 0) return append((byte)'0');

    int numlength = 1;
    boolean sign;

    // Claculate sign and length
    {
      if(l<0) { l = -l; sign = true; numlength++; } else sign = false;
      long m = l;
      while((m/=10) > 0) numlength++;
    }

    // Ensure capacity
    {
      int ll = length * 2, m = length + numlength;
      if(offset+m > data.length) ensureCapacity(ll>m?ll:m);
    }

    if(sign) data[offset+length] = (byte)'-';
    length += numlength;
    for(int i=offset+length-1; l>0; i--, l/=10)
      data[i] = (byte)((l % 10) + '0');

    return this;
  }


  /** Appends an <i>int</i> to the end of this ByteString.
   *
   * @return this string, modified.
   */

  public final ByteString append(int i) { return append((long)i); }


  /** Creates a substring out of this string by setting its leftmost
   * position to start and its rightmost position to end-1.
   *
   * @return this string, modified.
   */

  public final ByteString subSelf(int start, int end)
  {
    offset += start;
    length = end - start;
    return this;
  }


  /** Creates a substring out of this string by setting its leftmost
   * position to start.
   *
   * @return this string, modified.
   */

  public final ByteString subSelf(int start) { return subSelf(start, length); }


  /** Makes another string a substring of this string that shares
   * this string's character data.
   *
   * @return the other string, modified.
   */

  public final ByteString subView(int start, int end, ByteString n)
  {
    n.data = data;
    n.offset = offset + start;
    n.length = end - start;
    return n;
  }


  /** Makes another string a substring of this string that shares
   * this string's character data.
   *
   * @return the other string, modified.
   */

  public final ByteString subView(int start, ByteString n)
  {
    return subView(start, length, n);
  }


  /** Creates a new ByteString which is a substring of this string that shares
   * this string's character data.
   *
   * @return the new string.
   */

  public final ByteString subView(int start, int end)
  {
    return subView(start, end, new ByteString());
  }


  /** Creates a new ByteString which is a substring of this string that shares
   * this string's character data.
   *
   * @return the new string.
   */

  public final ByteString subView(int start)
  {
    return subView(start, length, new ByteString());
  }


  /** Creates a new ByteString which is a substring of this string.
   *
   * @return a new string.
   */

  public final ByteString subCopy(int start, int end)
  {
    ByteString n = new ByteString(end - start);
    n.length = end-start;
    System.arraycopy(data, offset+start, n.data, 0, n.length);
    return n;
  }


  /** Creates a new ByteString which is a substring of this string.
   *
   * @return a new string.
   */

  public final ByteString subCopy(int start) { return subCopy(start, length); }


  /** Makes another string a substring of this string.
   *
   * @return the other string, modified.
   */

  public final ByteString subCopy(int start, int end, ByteString other)
  {
    other.length = end-start;
    other.checkCapacity();
    System.arraycopy(data, offset+start, other.data, 0, other.length);
    return other;
  }


  /** Makes another string a substring of this string.
   *
   * @return the other string, modified.
   */

  public final ByteString subCopy(int start, ByteString other)
  {
    return subCopy(start, length, other);
  }


  /** Clears this string by setting its length to 0. */

  public final void clear() { length = 0; }


  /** Sets the length to 0 and detaches the data buffer. You have to call
   * this method before modifying this string if it is a view of another
   * string or if there is another string which is a view of this string.
   */

  public final void detach() { length = 0; data = null; }


  /**
   * @return true if the other string is a prefix of this string;
   *         false otherwise.
   */

  public final boolean startsWith(ConstByteString prefix)
  {
    if(prefix.length > length) return false;
    for(int i=0; i<prefix.length; i++)
      if(data[offset+i] != prefix.data[prefix.offset+i]) return false;
    return true;
  }


  /**
   * @return true if the other string is a suffix of this string;
   *         false otherwise.
   */

  public final boolean endsWith(ConstByteString suffix)
  {
    if(suffix.length > length) return false;
    for(int i=0; i<suffix.length; i++)
      if(data[offset+length-i-1] !=
	 suffix.data[suffix.offset+suffix.length-i-1])
	return false;
    return true;
  }


  /**
   * @return true if the other object is a ConstByteString and this string
   *         and the other string are equal; false otherwise.
   */

  public final boolean equals(Object other)
  {
    if(!(other instanceof ConstByteString)) return false;
    return equals((ConstByteString)other);
  }


  /**
   * @return true if this string and the other string are equal;
   *         false otherwise.
   *
   * @see #equalsIgnoreCase
   */

  public final boolean equals(ConstByteString other)
  {
    if(length != other.length) return false;
    for(int i=0; i<length; i++)
      if(data[offset+i] != other.data[other.offset+i]) return false;
    return true;
  }


  /**
   * @return true if this string and the other string are equal,
   *         ignoring case; false otherwise.
   *
   * @see #equals
   */

  public final boolean equalsIgnoreCase(ConstByteString other)
  {
    if(length != other.length) return false;
    for(int i=0; i<length; i++)
    {
      byte b1 = data[offset+i], b2 = other.data[other.offset+i];
      if(b1 != b2 && toUpperCase(b1) != toUpperCase(b2)) return false;
    }
    return true;
  }


  /**
   * @return a hashcode for this string.
   */

  public final int hashCode()
  {
    int h = 0;

    if(length < 16) for(int i=offset; i<length+offset; i++) h = h*37 + data[i];
    else
    {
      int skip = length / 8;
      for(int i=offset; i<length+offset; i+=skip) h = h*39 + data[i];
    }

    return h;
  }


  /**
   * @return the index of the first occurance of <i>b</i> in this string,
   *         or -1 if <i>b</i> does not occur.
   */

  public final int indexOf(byte b) { return indexOf(b, 0); }


  /**
   * @return the index of the first occurance of <i>b</i> in this string
   *         that is greater or equal to <i>fromIndex</i>,
   *         or -1 if <i>b</i> does not occur.
   */

  public final int indexOf(byte b, int fromIndex)
  {
    for(int i=fromIndex+offset; i<length+offset; i++)
      if(data[i] == b) return i-offset;
    return -1;
  }


  /**
   * @return the index of the first occurance of <i>c</i> in this string,
   *         or -1 if <i>c</i> does not occur.
   */

  public final int indexOf(char c) { return indexOf(c, 0); }


  /**
   * @return the index of the first occurance of <i>c</i> in this string
   *         that is greater or equal to <i>fromIndex</i>,
   *         or -1 if <i>c</i> does not occur.
   */

  public final int indexOf(char c, int fromIndex)
  {
    for(int i=fromIndex+offset; i<length+offset; i++)
      if(data[i] == c) return i-offset;
    return -1;
  }


  /**
   * @return the index of the last occurance of <i>b</i> in this string,
   *         or -1 if <i>b</i> does not occur.
   */

  public final int lastIndexOf(byte b) { return lastIndexOf(b, length-1); }


  /**
   * @return the index of the last occurance of <i>b</i> in this string
   *         that is less or equal to <i>fromIndex</i>,
   *         or -1 if <i>b</i> does not occur.
   */

  public final int lastIndexOf(byte b, int fromIndex)
  {
    for(int i=fromIndex+offset; i>=offset; i--)
      if(data[i] == b) return i-offset;
    return -1;
  }


  /**
   * @return the index of the last occurance of <i>c</i> in this string,
   *         or -1 if <i>c</i> does not occur.
   */

  public final int lastIndexOf(char c) { return lastIndexOf(c, length-1); }


  /**
   * @return the index of the last occurance of <i>c</i> in this string
   *         that is less or equal to <i>fromIndex</i>,
   *         or -1 if <i>c</i> does not occur.
   */

  public final int lastIndexOf(char c, int fromIndex)
  {
    for(int i=fromIndex+offset; i>=offset; i--)
      if(data[i] == c) return i-offset;
    return -1;
  }


  /**
   * @return the index of the first occurance of the other string in this
   *         string, or -1 if it does not occur.
   */

  public final int indexOf(ConstByteString other) { return indexOf(other, 0); }


  /**
   * @return the index of the first occurance of the other string in this
   *         string that is greater or equal to <i>fromIndex</i>,
   *         or -1 if it does not occur.
   */

  public final int indexOf(ConstByteString other, int fromIndex)
  {
    if(other.length == 0) return fromIndex;
    int max = length-other.length;
  test:
    for(int i=fromIndex; i<=max; i++)
    {
      int n = other.length;
      int j = i+offset;
      int k = other.offset;
      while(n-- != 0) if(data[j++] != other.data[k++]) continue test;
      return i;
    }
    return -1;
  }


  /**
   * @return the index of the first occurance of a String in this
   *         ByteString, or -1 if it does not occur.
   */

  public final int indexOf(String other) { return indexOf(other, 0); }


  /**
   * @return the index of the first occurance of a String in this
   *         ByteString that is greater or equal to <i>fromIndex</i>,
   *         or -1 if it does not occur.
   */

  public final int indexOf(String other, int fromIndex)
  {
    int olen = other.length();
    if(olen == 0) return fromIndex;
    int max = length-olen;
  test:
    for(int i=fromIndex; i<=max; i++)
    {
      int n = olen;
      int j = i+offset;
      int k = 0;
      while(n-- != 0) if(data[j++] != other.charAt(k++)) continue test;
      return i;
    }
    return -1;
  }


  /**
   * @return the index of the last occurance of the other string in this
   *         string, or -1 if it does not occur.
   */

  public final int lastIndexOf(ConstByteString other)
  {
    return lastIndexOf(other, length-1);
  }


  /**
   * @return the index of the last occurance of the other string in this
   *         string that is less or equal to <i>fromIndex</i>,
   *         or -1 if it does not occur.
   */

  public final int lastIndexOf(ConstByteString other, int fromIndex)
  {
    if(other.length == 0) return fromIndex;
    if(fromIndex > length-other.length) fromIndex = length-other.length;

    for(int i=fromIndex; i>=0; --i)
    {
      int n = other.length;
      int idx = i+offset;
      int idx2 = other.offset;
      while(data[idx++] == other.data[idx2++]) if(--n <= 0) return i;
    }
    return -1;
  }


  /** Determines if two string regions are equal. Case is important.
   *
   * @param toff the starting offset in this string.
   * @param other the other string.
   * @param ooff the starting offset in the other string.
   * @param len the number of bytes to compare.
   * @return true if the subregions match; false otherwise.
   */

  public final boolean regionMatches(int toff, ConstByteString other,
				     int ooff, int len)
  {
    return regionMatches(false, toff, other, ooff, len);
  }


  /** Determines if two string regions are equal.
   *
   * @param ignoreCase false if case is important; true otherwise.
   * @param toff the starting offset in this string.
   * @param other the other string.
   * @param ooff the starting offset in the other string.
   * @param len the number of bytes to compare.
   * @return true if the subregions match; false otherwise.
   */

  public final boolean regionMatches(boolean ignoreCase, int toff,
				     ConstByteString other, int ooff, int len)
  {
    if(toff+len > length) return false;
    if(ooff+len > other.length) return false;
    if(ignoreCase)
    {
      while(--len >= 0)
      {
	byte b1 = data[offset+toff++], b2 = other.data[other.offset+ooff++];
	if(b1 != b2 && toUpperCase(b1) != toUpperCase(b2)) return false;
      }
    }
    else
    {
      while(--len >= 0)
	if(data[offset+toff++] != other.data[other.offset+ooff++])
	  return false;
    }
    return true;
  }


  /** Replaces all occurences of <i>b1</i> by <i>b2</i>.
   *
   * @return this string, modified.
   */

  public final ByteString replaceSelf(byte b1, byte b2)
  {
    for(int i=offset; i<offset+length; i++) if(data[i] == b1) data[i] = b2;
    return this;
  }


  /** Creates a new string in which all occurences of <i>b1</i>
   * are replaced by <i>b2</i>.
   *
   * @return a new string.
   */

  public final ByteString replaceCopy(byte b1, byte b2)
  {
    ByteString n = new ByteString(length);
    n.length = length;
    for(int i=0; i<length; i++)
    {
      byte b = data[offset+i];
      if(b == b1) n.data[i] = b2; else n.data[i] = b;
    }
    return n;
  }


  /** Makes another string a copy of this string in which all
   * occurences of <i>b1</i> are replaced by <i>b2</i>.
   *
   * @return the other string, modified.
   */

  public final ByteString replaceCopy(byte b1, byte b2, ByteString other)
  {
    other.length = length;
    other.checkCapacity();
    for(int i=0; i<length; i++)
    {
      byte b = data[offset+i];
      if(b == b1) other.data[i] = b2; else other.data[i] = b;
    }
    return other;
  }


  /** Converts this string to lowercase.
   *
   * @return this string, modified.
   */

  public final ByteString toLowerCaseSelf()
  {
    for(int i=offset; i<offset+length; i++) data[i] = toLowerCase(data[i]);
    return this;
  }


  /** Converts this string to uppercase
   *
   * @return this string, modified.
   */

  public final ByteString toUpperCaseSelf()
  {
    for(int i=offset; i<offset+length; i++) data[i] = toUpperCase(data[i]);
    return this;
  }


  /** Creates a new string which is a lowercase version of this string.
   *
   * @return a new string.
   */

  public final ByteString toLowerCaseCopy()
  {
    ByteString n = new ByteString(length);
    n.length = length;
    for(int i=0; i<length; i++) n.data[i] = toLowerCase(data[offset+i]);
    return n;
  }


  /** Creates a new string which is an uppercase version of this string.
   *
   * @return a new string.
   */

  public final ByteString toUpperCaseCopy()
  {
    ByteString n = new ByteString(length);
    n.length = length;
    for(int i=0; i<length; i++) n.data[i] = toUpperCase(data[offset+i]);
    return n;
  }


  /**
   * @return a String representing this ByteString. The upper 8 bits of the
   *         String's characters are set to 0.
   */

  public final String toString()
  {
    return new String(data, 0, offset, length);
  }


  /**
   * @return a String representing this ByteString, converted with the
   *         specified encoding.
   *
   * @param enc a character encoding
   */

  public final String toString(String enc)
  {
    return new String(data, 0, offset, length);
  }


  /** Removes whitespace (all characters &lt;= 0x20) from both ends of
   * this string.
   *
   * @return this string, modified.
   */

  public final ByteString trim()
  {
    while(data[offset] <= (byte)0x20 && length > 0) { offset++; length--; }
    while(data[offset+length-1] <= (byte)0x20 && length > 0) length--;
    return this;
  }


  /**
   * @return the byte at the specified index.
   */

  public final byte byteAt(int i) { return data[offset+i]; }


  /** Sets the byte at the specified index to the specified value. */

  public final void setByteAt(int i, byte b) { data[offset+i] = b; }


  /** Sets the length of this string. If the new length is less than the
   * current length the string is truncated, otherwise 0x00 bytes are
   * appended at the end.
   */

  public final void setLength(int l)
  {
    if(l > length)
    {
      ensureCapacity(l);
      // for(int i=offset+length; i<offset+l; i++) data[i] = (byte)0;
    }
    length = l;
  }


  /** Reverses the character sequence in this string.
   *
   * @return this string, modified
   */

  public final ByteString reverse()
  {
    if(length != 0)
    {
      int a = offset, b = offset + length - 1;
      for(int i=0; i<length/2; i++)
      {
	byte tmp = data[offset+i];
	data[offset+i] = data[offset+length-1-i];
      data[offset+length-1-i] = tmp;
      }
    }
    return this;
  }


  /**
   * @return true if this string is empty; false otherwise.
   */

  public final boolean isEmpty() { return length == 0; }


  /** Reads a line terminated by \n, \r, \r\n or EOF
   * from a PushbackInputStream.
   *
   * @return false if EOF was the only byte that has been read;
   *         true otherwise.
   */

  public final boolean readLine(PushbackInputStream in) throws IOException
  {
    checkCapacity(128);
    length = 0;
    int c;

    while((c = in.read()) != -1)
    {
      switch(c)
      {
        case '\r':
	  int c2 = in.read();
	  if(c2 != '\n') in.unread(c2);
        case '\n':
	  return true;

        default:
	  if(length == data.length) ensureCapacity(length * 2);
	  data[length++] = (byte) c;
      }
    }
    if(length == 0) return false; else return true;
  }


  /** Reads a line terminated by \n, \r\n or EOF
   * from an InputStream.
   *
   * @return false if EOF was the only byte that has been read;
   *         true otherwise.
   */

  public final boolean readLine(InputStream in) throws IOException
  {
    checkCapacity(128);
    length = 0;
    int c;

    while((c = in.read()) != -1)
    {
      if(c == '\n') return true;
      else if(c != '\r')
      {
	if(length == data.length) ensureCapacity(length * 2);
	data[length++] = (byte) c;
      }
    }
    if(length == 0) return false; else return true;
  }


  /** Writes the content of this string to an OutputStream. */

  public final void printTo(OutputStream out) throws IOException
  {
    out.write(data, offset, length);
  }


  /** Writes the content of this string plus a \r\n line terminator
   * to an OutputStream.
   */

  public final void printcrlfTo(OutputStream out) throws IOException
  {
    out.write(data, offset, length);
    out.write(CRLF, 0, 2);
  }


  /** Writes the content of this string plus a \n line terminator
   * to an OutputStream.
   */

  public final void printlfTo(OutputStream out) throws IOException
  {
    out.write(data, offset, length);
    out.write((byte)'\n');
  }


  /** Creates an <i>int</i> representation of this string (radix 10).
   *
   * @exception java.lang.NumberFormatException if this string doesn't
   *            contain a number.
   */

  public final int toInt() throws NumberFormatException { return toInt(10); }


  /** Creates an <i>int</i> representation of this string.
   *
   * @param radix the radix of the string representation.
   * @exception java.lang.NumberFormatException if this string doesn't
   *            contain a number.
   */

  public final int toInt(int radix) throws NumberFormatException
  {
    int result = 0;
    boolean negative = false;
    int i = offset, m = offset+length;
    if(length > 0)
    {
      if(data[i] == (byte)'-') { negative = true; i++; }
      while(i < m)
      {
	int digit = data[i]-'0';
	if(digit<0 || digit>9)
	{
	  digit = data[i]-'A'+10;
	  if(digit<10 || digit>27) digit = data[i]-'a'+10;
	}
	if(digit<0 || digit>=radix)
	  throw new NumberFormatException(toString());
	result = result * radix + digit;
	i++;
      }
    }
    else throw new NumberFormatException("empty string");
    return negative? (-result) : result;
  }


  /**
   * @return the length of this string.
   */

  public final int length() { return length; }
}
