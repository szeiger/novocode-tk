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


public class MultipartBody extends Body
{
  private String boundary, oldBoundary;
  private boolean first = true;


  public MultipartBody(Entity e) throws MimeException
  {
    super(e);

    int pos = entity.contentTypeParams.toLowerCase().indexOf("boundary=");
    if(pos == -1) throw new MimeException("Multipart body without boundary.");
    boundary = entity.contentTypeParams.substring(pos+9).trim();
    if(boundary.charAt(0) == '"')
      boundary = boundary.substring(1,boundary.length()-1);
    boundary = boundary.trim();

    oldBoundary = entity.in.setBoundary(boundary);
    if(!entity.in.skipPreamble())
      throw new MimeException("Couldn't skip preamble.");
  }


  public Entity nextEntity() throws IOException, MimeException
  {
    if(!first)
      if(!entity.in.nextPart())
      {
	entity.in.setBoundary(oldBoundary);
	entity.in.nextPart();
	return null;
      }
    first = false;
    return new Entity(entity);
  }
}
