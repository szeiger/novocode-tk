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

// classes JdbcNFileManager, JdbcNFile, JdbcNFileOutputStream

import java.io.*;
import java.util.*;
import java.sql.*;


/**
 * A JdbcNFileManager provides access via NFile objects to file-like
 * resources which are stored in a relational database.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Immature.
 * </dl>
 *
 * @author Stefan Zeiger
 * @see com.novocode.tk.io.NFile
 */

public class JdbcNFileManager
{
  String url, user, password, table;
  Connection con;
  Statement stmt;


  /**
   * Creates a new JdbcNFileManager.
   *
   * <P>The specified table should contain at least the following columns:</P>
   * <TABLE BORDER=1>
   *   <TR><TH> Name </TH>
   *       <TH> Type    </TH>
   *       <TH> Description </TH></TR>
   *   <TR><TD> name </TD>
   *       <TD> VARCHAR NOT NULL PRIMARY KEY</TD>
   *       <TD> The resource name, including the full path </TD></TR>
   *   <TR><TD> parent </TD>
   *       <TD> VARCHAR NOT NULL</TD>
   *       <TD> The parent's name </TD></TR>
   *   <TR><TD> mtime </TD>
   *       <TD> BIGINT </TD>
   *       <TD> Last modification time in ms since epoch</TD></TR>
   *   <TR><TD> body </TD>
   *       <TD> BLOB</TD>
   *       <TD> The resource contents; NULL for a listable resource</TD></TR>
   * </TABLE>
   *
   * <P>Meta data is stored in columns of type VARCHAR whose name is the
   * meta name in lower case with "-" replaced by "_"; e.g. the meta
   * value "Content-Type" is stored in a column named "content_type".</P>
   *
   */

  public JdbcNFileManager(String url, String user, String password,
			  String table) throws NFileException
  {
    try
    {
      this.url = url;
      this.user = user;
      this.password = password;
      this.table = table;
      if(user != null)
	con = DriverManager.getConnection(url, user, password);
      else
	con = DriverManager.getConnection(url);
      stmt = con.createStatement();
    }
    catch(Exception e) { throw NFileException.getCascadeException(e); }
  }


  public void destroy()
  {
    try { stmt.close(); }
    catch(SQLException ignored) {}
    stmt = null;
    try { con.close(); }
    catch(SQLException ignored) {}
    con = null;
  }


  public NFile getNFile(String resource)
  {
    return new JdbcNFile(this, resource);
  }
}


class JdbcNFile extends NFile
{
  private JdbcNFileManager man;
  private String canonPath, parentPath;
  private NFile parent;


  JdbcNFile(JdbcNFileManager man, String path)
  {
    this.man = man;
    this.path = path;

    StringTokenizer tok = new StringTokenizer(path, "/");
    StringBuffer buf = new StringBuffer("/");
    Stack stack = new Stack();
    boolean more = tok.hasMoreTokens();
    while(tok.hasMoreTokens())
    {
      String t = tok.nextToken();
      if(t.equals("..") && !stack.empty()) stack.pop();
      else if(!t.equals(".")) stack.push(t);
    }
    int size = stack.size();
    for(int i=0; i<size; i++)
    {
      buf.append((String)stack.elementAt(i));
      if(i != size-1) buf.append('/');
    }

    canonPath = buf.toString();
    if(canonPath.length() > 1)
    {
      parentPath = canonPath.substring(0, canonPath.lastIndexOf('/'));
      if(parentPath.length() == 0) parentPath = "/";
    }
  }


  public String getCanonicalPath() throws NFileException { return canonPath; }


  public NFile getParent()
  {
    if(parent == null && parentPath != null)
      parent = new JdbcNFile(man, parentPath);
    return parent;
  }


  public NFile getSub(String name)
  {
    if(path.endsWith("/")) return new JdbcNFile(man, path + name);
    else return new JdbcNFile(man, path + '/' + name);
  }


  public boolean exists() throws NFileException
  {
    try
    {
      synchronized(man.con)
      {
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT name FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  return rs.next();
	}
	finally { if(rs != null) rs.close(); }
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public boolean hasContent() throws NFileException
  {
    try
    {
      synchronized(man.con)
      {
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT isnull(body) FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(!rs.next()) return false;
	  return rs.getInt(1) == 0;
	}
	finally { if(rs != null) rs.close(); }
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public boolean isListable() throws NFileException
  {
    try
    {
      synchronized(man.con)
      {
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT isnull(body) FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(!rs.next()) return false;
	  return rs.getInt(1) != 0;
	}
	finally { if(rs != null) rs.close(); }
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public long lastModified() throws NFileException
  {
    try
    {
      synchronized(man.con)
      {
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT mtime FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(!rs.next()) return -1;
	  return rs.getLong(1);
	}
	finally { if(rs != null) rs.close(); }
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public String[] list() throws NFileException
  {
    return list(null);
  }


  public String[] list(FilenameFilter filter) throws NFileException
  {
    if(!isListable()) return null;
    Vector v = new Vector();
    try
    {
      synchronized(man.con)
      {
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT name FROM "+man.table+
				     " WHERE parent=\""+canonPath+"\"");
	  while(rs.next())
	  {
	    String n = rs.getString(1);
	    n = n.substring(n.lastIndexOf('/') + 1);
	    if(filter == null || filter.accept(null, n)) v.addElement(n);
	  }
	}
	finally { if(rs != null) rs.close(); }
      }
      int vsize = v.size();
      String[] sa = new String[vsize];
      for(int i=0; i<vsize; i++) sa[i] = (String)v.elementAt(i);
      return sa;
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public long length() throws NFileException
  {
    try
    {
      synchronized(man.con)
      {
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT length(body) FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(!rs.next()) return -1;
	  return rs.getLong(1);
	}
	finally { if(rs != null) rs.close(); }
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public void createListable() throws NFileException
  {
    NFile par = getParent();
    if(par != null) par.createListable();

    try
    {
      synchronized(man.con)
      {
	boolean create;
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT isnull(body) FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(!rs.next()) create = true;
	  else if(rs.getInt(1) != 1)
	    throw new NFileException("Can't create dir \""+path+
				     "\". There's a file with that name.");
	  else create = false;
	}
	finally { if(rs != null) rs.close(); }
	if(create)
	{
	  man.stmt.executeUpdate
	    ("INSERT INTO "+man.table+" (name,parent,mtime) VALUES (\""+
	     canonPath+"\",\""+parentPath+"\",\""+System.currentTimeMillis()+
	     "\")");
	}
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public void moveTo(NFile dest) throws NFileException
  {
    boolean doSuper = true;
    if(dest instanceof JdbcNFile)
    {
      doSuper = false;
      JdbcNFile d = (JdbcNFile)dest;
      if(d.man != man)
      {
	if(!d.man.url.equals(man.url)) doSuper = true;
	if(!d.man.table.equals(man.table)) doSuper = true;
      }
    }
    if(doSuper) super.moveTo(dest);
    else
    {
      NFile destpar = dest.getParent();
      String destparpath = (destpar == null)? "" : destpar.getCanonicalPath();
      try
      {
	synchronized(man.con)
	{
	  man.stmt.executeUpdate
	    ("UPDATE "+man.table+" SET name=\""+dest.getCanonicalPath()+
	     "\",parent=\""+destparpath+"\" WHERE name=\""+canonPath+"\"");
	}
      }
      catch(SQLException e) { throw NFileException.getCascadeException(e); }
    }
  }


  public void delete() throws NFileException
  {
    try
    {
      synchronized(man.con)
      {
	man.stmt.executeUpdate
	  ("DELETE FROM "+man.table+" WHERE name=\""+canonPath+"\"");
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public InputStream getInputStream() throws NFileException
  {
    try
    {
      synchronized(man.con)
      {
	boolean close = true;
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT body FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(!rs.next()) throw new NFileNotFoundException();
	  InputStream in = rs.getBinaryStream(1);
	  if(in == null) in = new ByteArrayInputStream(rs.getBytes(1));
	  else close = false;
	  if(in == null)
	    throw new NFileException("Couldn't get InputStream");
	  return in;
	}
	finally { if(close) rs.close(); }
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public OutputStream getOutputStream() throws NFileException
  {
    if(!getParent().exists())
      throw new NFileNotFoundException
	("Parent directory doesn't exist anymore");
    return new JdbcNFileOutputStream(canonPath, parentPath, man, this);
  }


  public boolean hasMetaStore()
  {
    return true;
  }


  public String getMeta(String name) throws NFileException
  {
    String field = name.toLowerCase().replace('-','_');
    try
    {
      synchronized(man.con)
      {
	ResultSet rs = null;
	try
	{
	  rs = man.stmt.executeQuery("SELECT "+field+" FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(!rs.next()) return null;
	  return rs.getString(1);
	}
	finally { if(rs != null) rs.close(); }
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }


  public void setMeta(String name, String value) throws NFileException
  {
    String field = name.toLowerCase().replace('-','_');
    try
    {
      synchronized(man.con)
      {
	man.stmt.executeUpdate
	  ("UPDATE "+man.table+" SET "+field+"=\""+value+
	   "\" WHERE name=\""+canonPath+"\"");
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }
  }
}


class JdbcNFileOutputStream extends ByteArrayOutputStream
{
  private String canonPath, parentPath;
  private JdbcNFileManager man;
  private JdbcNFile nfile;


  JdbcNFileOutputStream(String canonPath, String parentPath,
			JdbcNFileManager man, JdbcNFile nfile)
  {
    this.canonPath = canonPath;
    this.parentPath = parentPath;
    this.man = man;
    this.nfile = nfile;
  }


  public void close() throws IOException
  {
    flush();

    try
    {
      synchronized(man.con)
      {
	boolean update;
	ResultSet rs = null;
	if(!nfile.getParent().exists())
	  throw new NFileNotFoundException
	    ("Parent directory doesn't exist anymore");
	try
	{
	  rs = man.stmt.executeQuery("SELECT isnull(body) FROM "+man.table+
				     " WHERE name=\""+canonPath+"\"");
	  if(rs.next()) update = true;
	  else update = false;
	}
	finally { if(rs != null) rs.close(); }

	PreparedStatement pstmt = null;
	try
	{
	  if(update)
	    pstmt = man.con.prepareStatement
	      ("UPDATE "+man.table+" SET mtime=\""+System.currentTimeMillis()+
	       "\",body=? WHERE name=\""+canonPath+"\"");
	  else
	    pstmt = man.con.prepareStatement
	      ("INSERT INTO "+man.table+" (name,parent,mtime,body) VALUES (\""+
	       canonPath+"\",\""+parentPath+"\",\""+System.currentTimeMillis()+
	       "\",?)");
	}
	catch(SQLException e) // DBMS doesn't support prepared statements
	{
	  if(update)
	    man.stmt.executeUpdate
	      ("UPDATE "+man.table+" SET mtime=\""+System.currentTimeMillis()+
	       "\",body=\""+getData()+"\" WHERE name=\""+canonPath+"\"");
	  else
	    man.stmt.executeUpdate
	      ("INSERT INTO "+man.table+" (name,parent,mtime,body) VALUES (\""+
	       canonPath+"\",\""+parentPath+"\",\""+System.currentTimeMillis()+
	       "\",\""+getData()+"\")");
	}
	if(pstmt != null)
	{
	  pstmt.setBinaryStream(1, new ByteArrayInputStream(buf,0,count),
				count);
	  pstmt.executeUpdate();
	  pstmt.close();
	}
      }
    }
    catch(SQLException e) { throw NFileException.getCascadeException(e); }

    super.close();
  }


  private String getData()
  {
    StringBuffer b = new StringBuffer(count + 16);
    for(int i=0; i<count; i++)
    {
      char c = (char)(buf[i] & 0xFF);
      switch(c)
      {
        case '\\': b.append("\\\\"); break;
        case '\0': b.append("\\0"); break;
        case '\'': b.append("\\\'"); break;
        case '\"': b.append("\\\""); break;
        default:   b.append(c);
      }
    }
    return b.toString();
  }
}
