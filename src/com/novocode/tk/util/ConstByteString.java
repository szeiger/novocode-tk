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

import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;


/**
 * This abstract class is extended by the ByteString class and provides
 * access to a subset of ByteString's methods to ensure that a
 * ConstByteString cannot be modified.
 *
 * <P>Note that casting a ConstByteString to ByteString, of course,
 * enables you to modify the ByteString. ConstByteString provides
 * safety for the programmer but not security. Also, the fields <I>data</I>,
 * <I>offset</I> and <I>length</I> can be modified directly. This is
 * necessary because fields, unlike methods, can not be redeclared from
 * <I>protected</I> to <I>public</I> in subclasses.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Relatively mature. Fixed API. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.util.ByteString
 */

public abstract class ConstByteString implements Serializable
{
  /** The data is used for character storage. */
  public byte[] data;

  /** The first index of the storage that is used. */
  public int offset;

  /** The number of characters in this string. */
  public int length;


  /* Avoid public constructor */
  protected ConstByteString() {}


  /** Compares this string lexicographically to another one.
   *
   * @return 0 if the strings are identical, a value less than 0
   *         if this string is lexicographically less than
   *         the other one, a value greater than 0 if this string
   *         is lexicographically greater than the other one.
   */

  public abstract int compareTo(ConstByteString other);


  /**
   * @return a new ByteString which is a concatenation of this string and
   *         the other string.
   */

  public abstract ByteString concat(ConstByteString other);


  /** Creates a new ByteString which is a substring of this string.
   *
   * @return a new string.
   */

  public abstract ByteString subCopy(int start, int end);


  /** Creates a new ByteString which is a substring of this string.
   *
   * @return a new string.
   */

  public abstract ByteString subCopy(int start);


  /** Makes another string a substring of this string.
   *
   * @return the other string, modified.
   */

  public abstract ByteString subCopy(int start, int end, ByteString other);


  /** Makes another string a substring of this string.
   *
   * @return the other string, modified.
   */

  public abstract ByteString subCopy(int start, ByteString other);


  /**
   * @return true if the other string is a prefix of this string;
   *         false otherwise.
   */

  public abstract boolean startsWith(ConstByteString prefix);


  /**
   * @return true if the other string is a suffix of this string;
   *         false otherwise.
   */

  public abstract boolean endsWith(ConstByteString suffix);


  /**
   * @return true if the other object is a ConstByteString and this string
   *         and the other string are equal; false otherwise.
   */

  public abstract boolean equals(Object other);


  /**
   * @return true if this string and the other string are equal;
   *         false otherwise.
   *
   * @see #equalsIgnoreCase
   */

  public abstract boolean equals(ConstByteString other);


  /**
   * @return true if this string and the other string are equal,
   *         ignoring case; false otherwise.
   *
   * @see #equals
   */

  public abstract boolean equalsIgnoreCase(ConstByteString other);


  /**
   * @return a hashcode for this string.
   */

  public abstract int hashCode();


  /**
   * @return the index of the first occurance of <i>b</i> in this string,
   *         or -1 if <i>b</i> does not occur.
   */

  public abstract int indexOf(byte b);


  /**
   * @return the index of the first occurance of <i>b</i> in this string
   *         that is greater or equal to <i>fromIndex</i>,
   *         or -1 if <i>b</i> does not occur.
   */

  public abstract int indexOf(byte b, int fromIndex);


  /**
   * @return the index of the first occurance of <i>c</i> in this string,
   *         or -1 if <i>c</i> does not occur.
   */

  public abstract int indexOf(char c);


  /**
   * @return the index of the first occurance of <i>c</i> in this string
   *         that is greater or equal to <i>fromIndex</i>,
   *         or -1 if <i>c</i> does not occur.
   */

  public abstract int indexOf(char c, int fromIndex);


  /**
   * @return the index of the last occurance of <i>b</i> in this string,
   *         or -1 if <i>b</i> does not occur.
   */

  public abstract int lastIndexOf(byte b);


  /**
   * @return the index of the last occurance of <i>b</i> in this string
   *         that is less or equal to <i>fromIndex</i>,
   *         or -1 if <i>b</i> does not occur.
   */

  public abstract int lastIndexOf(byte b, int fromIndex);


  /**
   * @return the index of the last occurance of <i>c</i> in this string,
   *         or -1 if <i>c</i> does not occur.
   */

  public abstract int lastIndexOf(char c);


  /**
   * @return the index of the last occurance of <i>c</i> in this string
   *         that is less or equal to <i>fromIndex</i>,
   *         or -1 if <i>c</i> does not occur.
   */

  public abstract int lastIndexOf(char c, int fromIndex);


  /**
   * @return the index of the first occurance of the other string in this
   *         string, or -1 if it does not occur.
   */

  public abstract int indexOf(ConstByteString other);


  /**
   * @return the index of the first occurance of the other string in this
   *         string that is greater or equal to <i>fromIndex</i>,
   *         or -1 if it does not occur.
   */

  public abstract int indexOf(ConstByteString other, int fromIndex);


  /**
   * @return the index of the first occurance of a String in this
   *         ByteString, or -1 if it does not occur.
   */

  public abstract int indexOf(String other);


  /**
   * @return the index of the first occurance of a String in this
   *         ByteString that is greater or equal to <i>fromIndex</i>,
   *         or -1 if it does not occur.
   */

  public abstract int indexOf(String other, int fromIndex);


  /**
   * @return the index of the last occurance of the other string in this
   *         string, or -1 if it does not occur.
   */

  public abstract int lastIndexOf(ConstByteString other);


  /**
   * @return the index of the last occurance of the other string in this
   *         string that is less or equal to <i>fromIndex</i>,
   *         or -1 if it does not occur.
   */

  public abstract int lastIndexOf(ConstByteString other, int fromIndex);


  /** Determines if two string regions are equal. Case is important.
   *
   * @param toff the starting offset in this string.
   * @param other the other string.
   * @param ooff the starting offset in the other string.
   * @param len the number of bytes to compare.
   * @return true if the subregions match; false otherwise.
   */

  public abstract boolean regionMatches(int toff, ConstByteString other,
					int ooff, int len);


  /** Determines if two string regions are equal.
   *
   * @param ignoreCase false if case is important; true otherwise.
   * @param toff the starting offset in this string.
   * @param other the other string.
   * @param ooff the starting offset in the other string.
   * @param len the number of bytes to compare.
   * @return true if the subregions match; false otherwise.
   */

  public abstract boolean regionMatches(boolean ignoreCase, int toff,
					ConstByteString other,
					int ooff, int len);


  /** Creates a new string in which all occurences of <i>b1</i>
   * are replaced by <i>b2</i>.
   *
   * @return a new string.
   */

  public abstract ByteString replaceCopy(byte b1, byte b2);


  /** Makes another string a copy of this string in which all
   * occurences of <i>b1</i> are replaced by <i>b2</i>.
   *
   * @return the other string, modified.
   */

  public abstract ByteString replaceCopy(byte b1, byte b2, ByteString other);


  /**
   * Converts a byte to lowercase.
   * The byte is expected to be an ISO-8859-1 (Latin-1) character.
   *
   * @param b a byte.
   * @return the lowercase version of the supplied byte if it exists;
   *         otherwise the same byte
   */

  public static byte toLowerCase(byte b)
  {
    // ASCII
    if((b >= (byte)'A') && (b <= (byte)'Z')) return (byte)(b+'a'-'A');
    if(b>=0) return b;
    // Latin-1
    int i = b&0xFF;
    if((i>=0xC0) && (i<=0xD6)) return (byte)(b+0xE0-0xC0);
    if((i>=0xD8) && (i<=0xDE)) return (byte)(b+0xF8-0xD8);
    return b;
  }


  /**
   * Converts a byte to uppercase.
   * The byte is expected to be an ISO-8859-1 (Latin-1) character.
   *
   * @param b a byte.
   * @return the uppercase version of the supplied byte if it exists;
   *         otherwise the same byte
   */

  public static byte toUpperCase(byte b)
  {
    // ASCII
    if((b >= (byte)'a') && (b <= (byte)'z')) return (byte)(b+'A'-'a');
    if(b>=0) return b;
    // Latin-1
    int i = b&0xFF;
    if((i>=0xE0) && (i<=0xF6)) return (byte)(b+0xC0-0xE0);
    if((i>=0xF8) && (i<=0xFE)) return (byte)(b+0xD8-0xF8);
    return b;
  }


  /** Creates a new string which is a lowercase version of this string.
   *
   * @return a new string.
   */

  public abstract ByteString toLowerCaseCopy();


  /** Creates a new string which is an uppercase version of this string.
   *
   * @return a new string.
   */

  public abstract ByteString toUpperCaseCopy();


  /**
   * @return the byte at the specified index.
   */

  public abstract byte byteAt(int i);


  /**
   * @return true if this string is empty; false otherwise.
   */

  public abstract boolean isEmpty();


  /** Writes the content of this string to an OutputStream. */

  public abstract void printTo(OutputStream out) throws IOException;


  /** Writes the content of this string plus a \r\n line terminator
   * to an OutputStream.
   */

  public abstract void printcrlfTo(OutputStream out) throws IOException;


  /** Writes the content of this string plus a \n line terminator
   * to an OutputStream.
   */

  public abstract void printlfTo(OutputStream out) throws IOException;


  /** Creates an <i>int</i> representation of this string (radix 10).
   *
   * @exception java.lang.NumberFormatException if this string doesn't
   *            contain a number.
   */

  public abstract int toInt() throws NumberFormatException;


  /** Creates an <i>int</i> representation of this string.
   *
   * @param radix the radix of the string representation.
   * @exception java.lang.NumberFormatException if this string doesn't
   *            contain a number.
   */

  public abstract int toInt(int radix) throws NumberFormatException;


  /**
   * @return the length of this string.
   */

  public abstract int length();
}
