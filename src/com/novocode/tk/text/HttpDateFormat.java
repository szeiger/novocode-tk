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


package com.novocode.tk.text;

import java.util.*;
import java.text.*;


/**
 * <CODE>HttpDateFormat</CODE> is a locale-independent date formatter
 * which can parse dates in the formats accepted by HTTP/1.1 (plus many
 * more formats and variants) and format dates in the HTTP/1.1 default
 * date format.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * New.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public final class HttpDateFormat extends SimpleDateFormat
{
  private static final SimpleTimeZone gmtTimeZone =
    new SimpleTimeZone(0, "GMT");

  private static final int numDays[] =
  { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };

  private static final int leapNumDays[] =
  { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 };


  public HttpDateFormat()
  {
    super("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
    setTimeZone(gmtTimeZone);
  }


  public Date parse(String text) throws ParseException
  {
    long l = parseDate(text.toCharArray());
    if(l == -1) throw new ParseException("Can't parse date", -1);
    else return new Date(l);
  }


  /**
   * Parses a date string.
   *
   * @param a the date string as a <EM>char[]</EM>.
   * @return millisenconds since epoch for the supplied date string, or -1
   *        if the date string could not be parsed.
   */

  public static long parseDate(char[] a)
  {
    int year = -1;
    int mon = -1;
    int mday = -1;
    int hour = -1;
    int min = -1;
    int sec = 0;

    int len = a.length;
    int start = 0;
    while(start < len)
    {
      char c = a[start];
      if(c == ';') break; // Skip everything after ';'
      else if(c == ' ' || c == ',') start++; // Skip whitespace and commas
      else if(c == '(') // Skip comments in parentheses
      {
	while(start < len && a[start] != ')') start++;
	start++;
      }
      else
      {
	// Skip to end of token
	int end = start + 1;
	while(end < len)
	{
	  char c1 = a[end];
	  if(c1 == ' ' || c1 == ',') break;
	  end++;
	}

	if(end == start + 1) // One-digit mday
	{
	  char c1 = a[start];
	  if(c1 >= '0' && c1 <= '9') mday = c1 - '0';
	}
	else if(end == start + 2) // Two-digit mday or year
	{
	  int ten = a[start] - '0', one = a[start + 1] - '0';
	  int i;
	  if(ten >= 0 && ten <= 9 && one >= 0 && one <= 9)
	  {
	    i = 10 * ten + one;
	    if(mday != -1 || i > 31)
	    {
	      if(i >= 70) year = 1900 + i;
	      else year = 2000 + i;
	    }
	    else mday = i;
	  }
	}
	else if(end == start + 3) // Abbreviated month name
	{
	  if(mon == -1) mon = parseShortMonth(a,start);
	}
	else if(end != start) // At least 4 characters
	{
	  int i1 = a[start] - '0', i2 = a[start + 1] - '0',
	    i3 = a[start + 2] - '0', i4 = a[start + 3] - '0';
	  if(i1 >= 0 && i1 <= 9 && i2 >= 0 && i2 <= 9 &&
	     i3 >= 0 && i3 <= 9 && i4 >= 0 && i4 <= 9) // 4-digit year
	    year = 1000*i1 + 100*i2 + 10*i3 + i4;
	  else
	  {
	    int dash = -1, colon = -1, slash = -1, dot = -1,
	      dash2 = -1, colon2 = -1, slash2 = -1, dot2 = -1;

	    for(int i=end-1; i>=start; i--)
	    {
	      if(a[i] == '-')      { dash2  = dash;  dash  = i; }
	      else if(a[i] == ':') { colon2 = colon; colon = i; }
	      else if(a[i] == '/') { slash2 = slash; slash = i; }
	      else if(a[i] == '.') { dot2   = dot;   dot   = i; }
	    }

	    if(dash != -1 &&
	       mon == -1 && mday == -1 && year == -1) // Probably DD-MMM-YY(YY)
	    {
	      if((dash == start+1 || dash == start+2) && (dash2 == dash+4))
	      {
		if(dash == start+1) mday = a[start]-'0';
		else
		  mday = 10 * (a[start]-'0') + (a[start+1]-'0');
		mon = parseShortMonth(a,dash+1);
		if(end == dash2+3)
		{
		  char yy1 = a[dash2+1], yy2 = a[dash2+2];
		  if(yy1 >= '0' && yy1 <= '9' && yy2 >= '0' && yy2 <= '9')
		  {
		    year = 10 * (yy1 - '0') + (yy2 - '0');
		    if(year >= 70) year += 1900; else year += 2000;
		  }
		}
		else if(end == dash2+5)
		{
		  int yy1 = a[dash2+1] - '0', yy2 = a[dash2+2] - '0',
		    yy3 = a[dash2+3] - '0', yy4 = a[dash2+4] - '0';
		  if(yy1 >= 0 && yy1 <= 9 && yy2 >= 0 && yy2 <= 9 &&
		     yy3 >= 0 && yy3 <= 9 && yy4 >= 0 && yy4 <= 9)
		    year = 1000*yy1 + 100*yy2 + 10*yy3 + yy4;
		}
		if(year == -1 || mon == -1) // Ignore token
		{
		  mon = -1;
		  mday = -1;
		  year = -1;
		}
	      }
	    }
	    else if(colon != -1 &&
		    hour == -1 && min == -1) // Probably HH:MM(:SS)
	    {
	      if(colon != start+1 && colon != start+2) break;
	      if(colon == start+1) hour = a[start]-'0';
	      else
		hour = 10 * (a[start]-'0') + (a[start+1]-'0');
	      char mm1 = a[colon+1], mm2 = (char)-1;
	      if(colon+2 < len) mm2 = a[colon+2];
	      if(mm1 >= '0' && mm1 <= '9')
	      {
		if(mm2 >= '0' && mm2 <= '9')
		  min = 10 * (mm1 - '0') + (mm2 - '0');
		else min = mm1 - '0';
	      }
	      if(colon2 != -1) // HH:MM:SS
	      {
		char ss1 = a[colon2+1], ss2 = (char)-1;
		if(colon2+2 < len) ss2 = a[colon2+2];
		if(ss1 >= '0' && ss1 <= '9')
		{
		  if(ss2 >= '0' && ss2 <= '9')
		    sec = 10 * (ss1 - '0') + (ss2 - '0');
		  else sec = ss1 - '0';
		}
	      }
	      if(min == -1) // Ignore token
	      {
		hour = -1;
		sec = 0;
	      }
	    }
	    else if(slash != -1 && mday == -1 &&
		    mon == -1 && year == -1) // Probably MM/DD/YY(YY)
	    {
	      if(slash != start+1 && slash != start+2) break;
	      if(slash == start+1) mon = a[start]-'0';
	      else
		mon = 10 * (a[start]-'0') + (a[start+1]-'0');
	      if(slash2 == slash+2)
	      {
		char dd1 = a[slash+1];
		if(dd1 >= '0' && dd1 <= '9') mday = dd1 - '0';
	      }
	      else if(slash2 == slash+3)
	      {
		char dd1 = a[slash+1], dd2 = a[slash+2];
		if(dd1 >= '0' && dd1 <= '9' && dd2 >= '0' && dd2 <= '9')
		  mday = 10 * (dd1 - '0') + (dd2 - '0');
	      }
	      if(mday != -1)
	      {
		if(end == slash2+3)
		{
		  char yy1 = a[slash2+1], yy2 = a[slash2+2];
		  if(yy1 >= '0' && yy1 <= '9' && yy2 >= '0' && yy2 <= '9')
		  {
		    year = 10 * (yy1 - '0') + (yy2 - '0');
		    if(year >= 70) year += 1900; else year += 2000;
		  }
		}
		else if(end == slash2+5)
		{
		  char yy1 = a[slash2+1], yy2 = a[slash2+2],
		    yy3 = a[slash2+3], yy4 = a[slash2+4];
		  if(yy1 >= '0' && yy1 <= '9' && yy2 >= '0' && yy2 <= '9' &&
		     yy3 >= '0' && yy3 <= '9' && yy4 >= '0' && yy4 <= '9')
		    year = 1000 * (yy1 - '0') + 100 * (yy2 - '0') +
		      10 * (yy3 - '0') + (yy4 - '0');
		}
		if(mon > 12) // Swap mon and mday
		{
		  int tmp = mon;
		  mon = mday;
		  mday = tmp;
		}
	      }
	      else // Ignore token
	      {
		mon = -1;
		year = -1;
	      }
	    }
	    else if(dot != -1 && mday == -1 &&
		    mon == -1 && year == -1) // Probably DD.MM.YY(YY)
	    {
	      if(dot != start+1 && dot != start+2) break;
	      if(dot == start+1) mday = a[start]-'0';
	      else
		mday = 10 * (a[start]-'0') + (a[start+1]-'0');
	      if(dot2 == dot+2)
	      {
		char dd1 = a[dot+1];
		if(dd1 >= '0' && dd1 <= '9') mon = dd1 - '0';
	      }
	      else if(dot2 == dot+3)
	      {
		char dd1 = a[dot+1], dd2 = a[dot+2];
		if(dd1 >= '0' && dd1 <= '9' && dd2 >= '0' && dd2 <= '9')
		  mon = 10 * (dd1 - '0') + (dd2 - '0');
	      }
	      if(mon != -1)
	      {
		if(end == dot2+3)
		{
		  char yy1 = a[dot2+1], yy2 = a[dot2+2];
		  if(yy1 >= '0' && yy1 <= '9' && yy2 >= '0' && yy2 <= '9')
		  {
		    year = 10 * (yy1 - '0') + (yy2 - '0');
		    if(year >= 70) year += 1900; else year += 2000;
		  }
		}
		else if(end == dot2+5)
		{
		  char yy1 = a[dot2+1], yy2 = a[dot2+2],
		    yy3 = a[dot2+3], yy4 = a[dot2+4];
		  if(yy1 >= '0' && yy1 <= '9' && yy2 >= '0' && yy2 <= '9' &&
		     yy3 >= '0' && yy3 <= '9' && yy4 >= '0' && yy4 <= '9')
		    year = 1000 * (yy1 - '0') + 100 * (yy2 - '0') +
		      10 * (yy3 - '0') + (yy4 - '0');
		}
		if(mon > 12) // Swap mon and mday
		{
		  int tmp = mon;
		  mon = mday;
		  mday = tmp;
		}
	      }
	      else // Ignore token
	      {
		mday = -1;
		year = -1;
	      }
	    }
	  }
	}

	start = end + 1;
      }
    }

    if(year < 0 || mon < 0 || mday < 0 || hour < 0 || min < 0) return -1;

    return
      (((((((mday - 1 + ((year-1970) * 365) +
	     year/4 - year/100 + year/400 - 477 +
	     (((year%4 == 0) && ((year%100 != 0) || (year%400 == 0))) ? 
	      leapNumDays[mon-1] : numDays[mon-1]))
	    * 24) + hour) * 60) + min) * 60L) + sec) * 1000L;
  }


  private static int parseShortMonth(char[] a, int pos)
  {
    char c1 = (char)(a[pos] | ('a'-'A'));
    char c2 = (char)(a[pos+1] | ('a'-'A'));
    char c3 = (char)(a[pos+2] | ('a'-'A'));

    if(c1 == 'j')
    {
      if(c2 == 'u')
      {
	if(c3 == 'n') return 6;
	if(c3 == 'l') return 7;
	return -1;
      }
      if(c2 == 'a' && c3 == 'n') return 1;
      return -1;
    }
    if(c1 == 'm')
    {
      if(c2 == 'a')
      {
	if(c3 == 'r') return 3;
	if(c3 == 'y') return 5;
      }
      return -1;
    }
    if(c1 == 'a')
    {
      if(c2 == 'p')
      {
	if(c3 == 'r') return 4;
      }
      else if(c2 == 'u')
      {
	if(c3 == 'g') return 8;
      }
      return -1;
    }
    if(c1 == 'f')
    {
      if(c2 == 'e' && c3 == 'b') return 2;
      return -1;
    }
    if(c1 == 's')
    {
      if(c2 == 'e' && c3 == 'p') return 9;
      return -1;
    }
    if(c1 == 'o')
    {
      if(c2 == 'c' && c3 == 't') return 10;
      return -1;
    }
    if(c1 == 'n')
    {
      if(c2 == 'o' && c3 == 'v') return 11;
      return -1;
    }
    if(c1 == 'd' && c2 == 'e' && c3 == 'c') return 12;
    return -1;
  }


  public boolean equals(Object obj)
  {
    return obj instanceof HttpDateFormat;
  }


  public int hashCode()
  {
    return 42;
  }


  //  public static void main(String[] args) throws Exception
  //  {
  //    long htime = 0, dtime = 0;
  //
  //    HttpDateFormat f = new HttpDateFormat();
  //
  //    for(int i=0; i<args.length; i++)
  //    {
  //      System.out.print("HttpDateFormat...: " + args[i] + " ==> ");
  //      Date d = f.parse(args[i]);
  //      System.out.println(f.format(d));
  //      System.out.print("Date.parse().....: " + args[i] + " ==> ");
  //      long l = Date.parse(args[i]);
  //      System.out.println(f.format(new Date(l)));
  //
  //      long t0 = System.currentTimeMillis();
  //      for(int j=0; j<5000; j++)
  //        HttpDateFormat.parseDate(args[i].toCharArray());
  //      htime += System.currentTimeMillis()-t0;
  //
  //      t0 = System.currentTimeMillis();
  //      for(int j=0; j<5000; j++) Date.parse(args[i]);
  //      dtime += System.currentTimeMillis()-t0;
  //    }
  //
  //    System.out.println("HttpDateFormat took "+(htime/1000.0)+" secs.");
  //    System.out.println("Date.parse took     "+(dtime/1000.0)+" secs.");
  //    System.out.println("Factor: "+((dtime*1.0)/htime));
  //  }
}
