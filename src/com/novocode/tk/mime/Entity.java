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

import java.io.*;
import java.util.*;

import com.novocode.tk.util.HeaderParser;
import com.novocode.tk.io.Latin1InputStreamReader;


public class Entity
{
  String contentType, contentTransferEncoding, contentTypeParams;
  BoundaryInputStream in;
  private Latin1InputStreamReader asciiReader;
  private Dictionary headers;
  private Entity superEntity;


  Entity(Entity e) throws IOException, MimeException
  {
    this.in = e.in;
    this.asciiReader = e.asciiReader;
    superEntity = e;
    init();
  }

  public Entity(BoundaryInputStream in) throws IOException, MimeException
  {
    this(in, null);
  }

  public Entity(BoundaryInputStream in, Dictionary headers)
         throws IOException, MimeException
  {
    this.in = in;
    this.asciiReader = new Latin1InputStreamReader(in);
    this.headers = headers;
    init();
  }


  private void init() throws IOException, MimeException
  {
    if(headers == null)
    {
      headers = new Hashtable(10);
      HeaderParser.parseHeader(this.asciiReader, headers);
    }

    contentType = (String)headers.get("content-type");
    if(contentType != null)
    {
      int sep = contentType.indexOf(';');
      if(sep != -1)
      {
	contentTypeParams = contentType.substring(sep);
	contentType = contentType.substring(0,sep);
      }
      contentType = contentType.trim().toLowerCase();
    }
    else if(superEntity != null &&
	    superEntity.contentType.equals("multipart/digest"))
      contentType = "message/rfc822";
    else contentType = "text/plain";

    contentTransferEncoding =
      ((String)headers.get("content-transfer-encoding"));
    if(contentTransferEncoding != null)
      contentTransferEncoding = contentTransferEncoding.toLowerCase();
  }


  public String get(String header) { return (String)headers.get(header); }

  public String getContentType() { return contentType; }

  public String getContentTypeParams() { return contentTypeParams; }

  public String getContentTransferEncoding() { return contentTransferEncoding;}

  public String getContentID() { return (String)headers.get("content-id"); }

  public String getContentDescription()
  {
    return (String)headers.get("content-description"); //-- decode via RFC1522
  }

  public Body getBody() throws MimeException
  {
    if(contentType.startsWith("multipart/")) return new MultipartBody(this);
    else if(contentType.startsWith("message/")) return new MessageBody(this);
    else return new UnknownBody(this);
  }


  private static void testEntity(Entity e, int depth)
          throws IOException, MimeException
  {
    for(int i=0; i<depth*2; i++) System.out.print(' ');
    System.out.println(e.getContentType());
    Body b = e.getBody();
    if(b instanceof MultipartBody)
    {
      Entity sub;
      while((sub = ((MultipartBody)b).nextEntity()) != null)
	testEntity(sub,depth+1);
    }
    else if(b instanceof MessageBody)
      testEntity(((MessageBody)b).getEntity(),depth+1);
    else
    {
      System.out.println("--------------------<START>--------------------");
      int c;
      InputStream in = b.getInputStream();
      while((c = in.read()) != -1) System.out.print((char)c);
      System.out.println("---------------------<END>---------------------");
    }
  }


  public static void main(String[] args) throws IOException, MimeException
  {
    int c;
    File file = new File(args[0]);
    BoundaryInputStream in =
      new BoundaryInputStream(new FileInputStream(file), (int)file.length());
    Entity e = new Entity(in);
    testEntity(e,0);
  }
}
