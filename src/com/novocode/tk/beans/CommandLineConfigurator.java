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


package com.novocode.tk.beans;

import java.lang.reflect.*;
import java.beans.*;
import java.util.Vector;
import java.util.Hashtable;
import java.io.PrintStream;


/**
 * A class for configuring Beans and other objects via command line
 * options. A well-written bean can be configured almost
 * <em>automatically</em> with this tool.
 * Indexed properties, default options and all kinds of primitive and
 * class value types are supported. Command line arguments can be
 * specified as one-letter POSIX or long GNU options.
 *
 * <p><dl><dt><b>Maturity:</b><dd>
 * Intermediate. Fully documented.
 * </dl>
 *
 * @author Stefan Zeiger
 */

public class CommandLineConfigurator
{
  private static final Object[] EMPTY_ARGS = new Object[0];

  private static final String EOL = System.getProperty("line.separator");
  private static final String AUTOMATIC = new String();

  private static final Hashtable wrapperClasses = new Hashtable();
  {
    wrapperClasses.put(boolean.class, Boolean.class);
    wrapperClasses.put(byte.class, Byte.class);
    wrapperClasses.put(char.class, Character.class);
    wrapperClasses.put(double.class, Double.class);
    wrapperClasses.put(float.class, Float.class);
    wrapperClasses.put(int.class, Integer.class);
    wrapperClasses.put(long.class, Long.class);
    wrapperClasses.put(short.class, Short.class);
  }


  private Object target;
  private Vector optsv = new Vector();
  private String topText = AUTOMATIC, bottomText =
  EOL+"Boolean options can take the following forms:"+EOL+
  "  TRUE: --opt=true, --opt, -o; FALSE: --opt=false, +o"+EOL+
  "Other options can take the following forms:"+EOL+
  "  --opt=value, --opt value, -o=value, -o value";
  private Option[] shortOptions = new Option[128];
  private Hashtable longOptions = new Hashtable();
  private int maxSignedLongNameLength;
  private boolean addedProps = false, autoAddProps;
  private BeanInfo beanInfo;
  private String defaultOptionLongName;
  private int defaultOptionIndex = -1;
  private PropertyDescriptor[] props;
  private Hashtable formConverters = new Hashtable();
  private ExternalFormConverter defaultConverter =
    new UniversalExternalFormConverter();


  /**
   * Creates a new CommandLineConfigurator.
   *
   * This method does the same as
   * <EM>CommandLineConfigurator(target, true)</EM>.
   *
   * @see #CommandLineConfigurator(java.lang.Object, boolean)
   */

  public CommandLineConfigurator(Object target) throws IntrospectionException
  {
    this(target, true);
  }


  /**
   * Creates a new CommandLineConfigurator.
   *
   * A callback option "-h" / "--help" which calls
   * <EM>printHelp(System.err)</EM> and then exits is added
   * automatically before any other option.
   *
   * @param target the object to configure.
   * @param introspect true if the object should be introspected to find
   *        the available parameters.
   * @exception IntrospectionException if <EM>introspect</EM> is true and
   *        something goes wrong while introspecting the object.
   */

  public CommandLineConfigurator(Object target, boolean introspect)
         throws IntrospectionException
  {
    this.target = target;

    addCallback("help", 'h', "print this page and exit", new Runnable(){
      public void run() { printHelp(System.err); System.exit(0); }
    });

    if(introspect)
    {
      beanInfo = Introspector.getBeanInfo(target.getClass());
      props = beanInfo.getPropertyDescriptors();
      defaultOptionIndex = beanInfo.getDefaultPropertyIndex();
      if(defaultOptionIndex != -1)
	defaultOptionLongName =
	  makeLongName(props[defaultOptionIndex].getName());
    }
  }


  private void autoAddProps()
  {
    if(!autoAddProps) return;

    if(addedProps) return;
    addedProps = true;
    if(props == null) return;

    for(int i=0; i<props.length; i++)
    {
      if(i == defaultOptionIndex) continue;
      Method setter = props[i].getWriteMethod();
      if(setter != null)
      {
	addOption(makeLongName(props[i].getName()),
		  props[i].getReadMethod(),
		  props[i].getWriteMethod(),
		  props[i].getPropertyType(),
		  null,
		  0,
		  props[i].getShortDescription());
      }
    }
  }


  /**
   * Creates a custom option.
   *
   * @param longName the option's long name (without the initial dashes).
   * @param getter a method of the target object which returns the
   *        value of the option. The method takes no arguments and
   *        returns a value of the type specified by the <EM>type</EM>
   *        argument.
   * @param setter a method of the target object which sets the
   *        value of the option. The method takes one argument of the
   *        type specified by the <EM>type</EM> argument.
   * @param type the type of the option.
   * @param typeStr the type name which is displayed on the help screen, or
   *        null to create the name automatically from the option type.
   * @param abbrev the option's short name as a <EM>char</EM>, 0 for an
   *        automatically selected short name or -1 for no short name.
   * @param desc a short line of text which describes the option. This
   *        description is included in the automatically generated help
   *        page.
   */

  public void addOption(String longName, Method getter, Method setter,
			Class type, String typeStr, int abbrev, String desc)
  {
    Option o = new Option();
    o.longName = longName;
    o.setter = setter;
    o.getter = getter;
    o.type = type;
    o.typeStr = typeStr;
    o.abbrev = abbrev;
    o.desc = desc;
    o.init();
  }


  /**
   * Creates a property option.
   *
   * @param longName the option's long name (without the initial dashes) or
   *        null to create the long name automatically from the property name.
   * @param pd the bean property which is modified by the new option.
   * @param abbrev the option's short name as a <EM>char</EM>, 0 for an
   *        automatically selected short name or -1 for no short name.
   * @param typeStr the type name which is displayed on the help screen, or
   *        null to create the name automatically from the option type.
   * @param desc a short line of text which describes the option. This
   *        description is included in the automatically generated help
   *        page.
   */

  public void addPropertyOption(String longName, PropertyDescriptor pd,
				String typeStr, int abbrev, String desc)
  {
    if(longName == null) longName = makeLongName(pd.getName());
    addOption(longName, pd.getReadMethod(), pd.getWriteMethod(),
	      pd.getPropertyType(), typeStr, abbrev, desc);
  }


  /**
   * Creates a property option.
   *
   * @param longName the option's long name (without the initial dashes) or
   *        null to create the long name automatically from the property name.
   * @param propertyName the name of the bean property which is modified by
   *        the new option.
   * @param typeStr the type name which is displayed on the help screen, or
   *        null to create the name automatically from the option type.
   * @param abbrev the option's short name as a <EM>char</EM>, 0 for an
   *        automatically selected short name or -1 for no short name.
   * @param desc a short line of text which describes the option. This
   *        description is included in the automatically generated help
   *        page.
   * @return true if the option was successfully created; false if the
   *        target was not introspected or the named property was not found.
   */

  public boolean addPropertyOption(String longName, String propertyName,
				   String typeStr, int abbrev, String desc)
  {
    if(props == null) return false;

    for(int i=0; i<props.length; i++)
    {
      PropertyDescriptor pd = props[i];
      if(pd.getName().equals(propertyName))
      {
	addPropertyOption(longName, pd, typeStr, abbrev, desc);
	return true;
      }
    }
    return false;
  }


  /**
   * Creates a property option.
   *
   * Calls <CODE>addPropertyOption(longName, propertyName, null, abbrev,
   * desc)</CODE>.
   *
   * @param longName the option's long name (without the initial dashes) or
   *        null to create the long name automatically from the property name.
   * @param propertyName the name of the bean property which is modified by
   *        the new option.
   * @param abbrev the option's short name as a <EM>char</EM>, 0 for an
   *        automatically selected short name or -1 for no short name.
   * @param desc a short line of text which describes the option. This
   *        description is included in the automatically generated help
   *        page.
   * @return true if the option was successfully created; false if the
   *        target was not introspected or the named property was not found.
   */

  public boolean addPropertyOption(String longName, String propertyName,
				   int abbrev, String desc)
  {
    return addPropertyOption(longName, propertyName, null, abbrev, desc);
  }


  /**
   * Creates a callback option.
   *
   * @param longName the option's long name (without the initial dashes).
   * @param abbrev the option's short name as a <EM>char</EM>, 0 for an
   *        automatically selected short name or -1 for no short name.
   * @param desc a short line of text which describes the option. This
   *        description is included in the automatically generated help
   *        page.
   * @param callback an object whose <EM>run()</EM> method is called when
   *        the option is specified.
   */

  public void addCallback(String longName, int abbrev, String desc,
			  Runnable callback)
  {
    Option o = new Option();
    o.longName = longName;
    o.abbrev = abbrev;
    o.desc = desc;
    o.callback = callback;
    o.init();
  }


  /**
   * Removes the specified option.
   */

  public void removeOption(String longName)
  {
    Option o = (Option)longOptions.remove(longName);
    if(o != null)
    {
      if(o.abbrev >= 0) shortOptions[o.abbrev] = null;
      optsv.removeElement(o);
    }
  }


  /**
   * Configures the target object.
   *
   * @param args your application's command line arguments as passed
   *        to the <EM>main()</EM> method.
   */

  public synchronized void configure(String[] args)
  {
    autoAddProps();
    for(int i=0; i<args.length; i++)
    {
      String a = args[i], optArg = null;
      Option opt = null;
      boolean neg = false;
      if(a.startsWith("--"))
      {
	int eqidx = a.indexOf('=');
	if(eqidx == -1) opt = (Option)longOptions.get(a.substring(2));
	else
	{
	  opt = (Option)longOptions.get(a.substring(2, eqidx));
	  optArg = a.substring(eqidx+1);
	}
      }
      else if(a.length()>=2)
      {
	boolean found = false;
	if(a.charAt(0) == '+') { found = true; neg = true; }
	else if(a.charAt(0) == '-') found = true;
	if(found)
	{
	  char c = a.charAt(1);
	  if(c >= 0 && c <= shortOptions.length) opt = shortOptions[c];
	  int eqidx = a.indexOf('=');
	  if(eqidx != -1) optArg = a.substring(eqidx+1);
	}
      }

      if(opt == null && defaultOptionLongName != null)
      {
	opt = (Option)longOptions.get(defaultOptionLongName);
	optArg = a;
      }

      if(opt == null) printError(System.err, a, "Unknown option", null);
      else if(opt.callback != null) opt.callback.run(); // invoke callback
      else // invoke setter method
      {
	/* use next commandline argument as option argument, if applicable */
	if(optArg == null)
	{
	  if(opt.type.equals(Boolean.class)) optArg = neg? "false":"true";
	  else
	  {
	    if(i+1 < args.length) optArg = args[++i];
	    else printError(System.err, a, "Unknown option", null);
	  }
	}

	try { opt.setOption(optArg); }
	catch(NoSuchMethodException e)
	{
	  printError(System.err, opt.longName, "Couldn't convert argument \""+
		     optArg+"\" to type "+opt.type.getName()+':', e);
	}
	catch(Exception e)
	{
	  Throwable t = e;
	  if(e instanceof InvocationTargetException)
	    t = ((InvocationTargetException)e).getTargetException();
	  printError(System.err, opt.longName, null, t);
	}
      }
    }
    for(int i=0; i<optsv.size(); i++)
    {
      Option o = (Option)optsv.elementAt(i);
      try { o.postProcessIndexed(); }
      catch(Exception e)
      {
	printError(System.err, o.longName, null, e);
      }
    }
  }


  /**
   * Prints an error message when an option can't be set. The default
   * implementation prints "Couldn't set option" plus the option name,
   * the optional error message, the stack trace and "Start with option
   * --help to get a list of available options.", and then calls
   * <EM>System.exit(1)</EM>.
   *
   * This method can be overridden in subclasses to provide custom
   * error messages.
   */

  protected void printError(PrintStream out, String opt,
			    String msg, Throwable t)
  {
    out.print("Couldn't set option \""+opt+"\"");
    if(msg != null || t != null)
    {
      out.println(": ");
      if(msg != null) out.println(msg);
      if(t != null) t.printStackTrace(out);
    }
    else out.println();
    out.println();
    out.println
      ("Start with option --help to get a list of available options.");
    System.exit(1);
  }


  /**
   * Prints a help page which shows the options, descriptions and
   * current/default values.
   */

  public void printHelp(PrintStream out)
  {
    String tt = getTopText();
    if(tt != null) out.println(tt);
    for(int i=0; i<optsv.size(); i++)
    {
      Option o = (Option)optsv.elementAt(i);
      if(o.abbrev == -1) out.print("       --");
      else
      {
	out.print("  -");
	out.print((char)(o.abbrev));
	out.print(",  --");
      }
      out.print(o.longName);
      if(o.typeStr != null)
      {
	out.print('=');
	out.print(o.typeStr);
      }
      for(int j=o.signedLongNameLength; j<maxSignedLongNameLength+2; j++)
	out.print(' ');
      if(o.getter != null && (!o.isIndexed))
      {
	Object def;
	try { def = o.getter.invoke(target, EMPTY_ARGS); }
	catch(Throwable e) { def = "<error reading default>"; }
	out.print(o.desc);
	out.print(" (");
	out.print(findConverter(o.longName, o.type)
		  .toExternalForm(o.type, def));
	out.println(')');
      }
      else out.println(o.desc);
    }
    if(bottomText != null) out.println(bottomText);
  }


  /**
   * Sets an external form converter to be used for the specified argument.
   *
   * Converters for specific arguments take precedence over converters for
   * specific types. If neither was found, the default converter is used.
   */

  public void setConverter(String longName, ExternalFormConverter conv)
  {
    formConverters.put(longName, conv);
  }


  /**
   * Sets an external form converter to be used for the specified type.
   *
   * Converters for specific arguments take precedence over converters for
   * specific types. If neither was found, the default converter is used.
   */

  public void setConverter(Class type, ExternalFormConverter conv)
  {
    formConverters.put(type, conv);
  }


  /**
   * Sets the external form converter to be used when no special converter
   * was found for a type or argument name. The default is a
   * <EM>UniversalExternalFormConverter</EM>.
   *
   * @see com.novocode.tk.beans.UniversalExternalFormConverter
   */

  public void setDefaultConverter(ExternalFormConverter conv)
  {
    defaultConverter = conv;
  }


  private ExternalFormConverter findConverter(String longName, Class type)
  {
    ExternalFormConverter c;
    c = (ExternalFormConverter)formConverters.get(longName);
    if(c != null) return c;
    c = (ExternalFormConverter)formConverters.get(type);
    if(c != null) return c;
    return defaultConverter;
  }


  /**
   * Changes the text which is printed on the help screen above the options.
   */

  public void setTopText(String s) { topText = s; }


  /**
   * Returns the text which is printed on the help screen above the options.
   */

  public String getTopText()
  {
    if(topText != AUTOMATIC) return topText;
    else if(defaultOptionLongName == null)
      return
	"Usage: java "+target.getClass().getName()+" [OPTION]..."+
	EOL+EOL+"Options:";
    else
      return
	"Usage: java "+target.getClass().getName()+" [OPTION | "+
	defaultOptionLongName+"]..."+EOL+EOL+"Options:";
  }


  /**
   * Changes the text which is printed on the help screen below the options.
   */

  public void setBottomText(String s) { bottomText = s; }


  /**
   * Returns the text which is printed on the help screen below the options.
   */

  public String getBottomText() { return bottomText; }


  /**
   * Activates or deactivates the automatic property options adding
   * feature.
   *
   * Property options are added with the first call to <EM>configure()</EM>.
   * The default is not to add property options automatically.
   *
   * @see #configure
   */

  public void setAutoAddPropertyOptions(boolean b) { autoAddProps = b; }


  /**
   * Returns if options for properties are added automatically.
   */

  public boolean getAutoAddPropertyOptions() { return autoAddProps; }


  /**
   * Changes the default option which can be specified on the command line
   * without a leading option name.
   *
   * @param longName the long name of the new default option or null for
   *        no default option.
   */

  public void setDefaultOption(String longName)
  {
    defaultOptionLongName = longName;
  }


  /**
   * Returns the default option which can be specified on the command line
   * without a leading option name.
   *
   * @return the long name of the default option or null for
   *         no default option.
   */

  public String getDefaultOption() { return defaultOptionLongName; }


  private int makeAbbrev(String s)
  {
    int slen = s.length();
    for(int i=0; i<slen; i++)
    {
      char c = s.charAt(i);
      if(shortOptions[c] == null) return c;
    }
    for(char c='a'; c<='z'; c++) if(shortOptions[c] == null) return c;
    for(char c='A'; c<='Z'; c++) if(shortOptions[c] == null) return c;
    return -1;
  }


  static String makeLongName(String s)
  {
    int slen = s.length();
    StringBuffer b = new StringBuffer(slen+4);
    boolean wasUpper = true;
    for(int i=0; i<slen; i++)
    {
      char c = s.charAt(i);
      if(Character.isUpperCase(c))
      {
	if(!wasUpper && i!=0) b.append('-');
	b.append(Character.toLowerCase(c));
	wasUpper = true;
      }
      else
      {
	b.append(c);
	wasUpper = false;
      }
    }
    return b.toString();
  }


  private class Option
  {
    String longName, desc, typeStr;
    int signedLongNameLength, abbrev;
    Class type, primitiveType;
    Method getter, setter;
    boolean isIndexed;
    Runnable callback;
    Vector indexedValues;


    void init()
    {
      if(abbrev == 0) abbrev = makeAbbrev(longName);
      signedLongNameLength = longName.length();

      if(type != null)
      {
	if(!type.equals(boolean.class))
	{
	  if(type.isArray())
	  {
	    type = type.getComponentType();
	    isIndexed = true;
	  }
	  primitiveType = type;
	  if(typeStr == null)
	    typeStr = findConverter(longName, type).getExternalTypeName(type);
	  if(isIndexed) typeStr += "[]";
	  signedLongNameLength += typeStr.length() + 1;
	}
	Class wrapperType = (Class)wrapperClasses.get(type);
	if(wrapperType != null) type = wrapperType;
      }

      if(signedLongNameLength > maxSignedLongNameLength)
	maxSignedLongNameLength = signedLongNameLength;
      optsv.addElement(this);
      longOptions.put(longName, this);
      if(abbrev > 0) shortOptions[abbrev] = this;
    }


    void setOption(String optArg) throws NoSuchMethodException,
      IllegalAccessException,
      IllegalArgumentException,
      InvocationTargetException
    {
      Object val = findConverter(longName, type).toObject(type, optArg);

      if(isIndexed)
      {
	if(indexedValues == null) indexedValues = new Vector();
	indexedValues.addElement(val);
      }
      else setter.invoke(target, new Object[] { val });
    }


    void postProcessIndexed() throws IllegalAccessException,
      IllegalArgumentException, InvocationTargetException
    {
      if(indexedValues == null) return;

      try
      {
	int s = indexedValues.size();
	Object val = Array.newInstance(primitiveType, s);
	for(int i=0; i<s; i++) Array.set(val, i, indexedValues.elementAt(i));
	setter.invoke(target, new Object[] { val });
      }
      catch(NegativeArraySizeException ignored) {}
    }
  }
}
