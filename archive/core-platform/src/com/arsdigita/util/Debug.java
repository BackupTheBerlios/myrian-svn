/*
 * Copyright (C) 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Collection of miscellaneous utility methods that may occasionally aid
 * debugging.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-23
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/Debug.java#5 $ $Date: 2003/06/25 $
 **/
public class Debug {
    private static final Logger s_log = Logger.getLogger(Debug.class);

    private Debug() {}

    /**
     * Useful if you want to examine the string <code>text</code> in an
     * editor. Typical use pattern:
     *
     * <pre>
     * Debug.dumpToFile("/tmp/suspect-string.txt", suspectString);
     * </pre>
     *
     * <p>Possible IO exceptions will be logged but not rethrown. </p>
     **/
    public static void dumpToFile(String filename, String text) {
        Writer writer = getWriter(filename);
        if ( writer == null ) {
            return;
        }

        try {
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            s_log.error("Couldn't dump text to file", ex);
        }
    }


    /**
     * Dump a character array to the file.
     *
     * @see #dumpToFile(String, String)
     **/
    public static void dumpToFile(String filename, char[] chars) {
        Writer writer = getWriter(filename);
        if ( writer == null ) {
            return;
        }

        try {
            writer.write(chars);
            writer.close();
        } catch (IOException ex) {
            s_log.error("Couldn't dump text to file", ex);
        }
    }

    /**
     * Returns a <em>new</em> writer every time you call it.
     **/
    private static Writer getWriter(String filename) {
        return getWriter(filename, false);
    }

    /**
     * Returns a <em>new</em> writer every time you call it.
     **/
    private static Writer getWriter(String filename, boolean append) {
        try {
            return new FileWriter(filename, append);
        } catch (IOException ex) {
            s_log.error("Couldn't create file writer", ex);
        }
        return null;
    }

    public static String readFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line=br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException ex) {
            s_log.error("Couldn't read " + filename, ex);
        }
        return null;
    }

    /**
     * This method allows you to access a private field named
     * <code>fieldName</code> of the object <code>obj</code>.
     *
     * <p>Example usage:</p>
     *
     * <pre>
     * public void doStuff(Foo foo) {
     *     Baz baz = (Baz) Debug.getPrivateField(Foo.class, foo, "m_baz");
     *     System.err.println("foo's private field m_baz is " + baz);
     *     // do stuff with foo
     *     // ...
     * }
     * </pre>
     *
     * @see java.lang.reflect.AccessibleObject#setAccessible(boolean)
     * @see java.lang.reflect.ReflectPermission
     *
     * @param klass the class of <code>obj</code>
     * @param obj the object whose field is being accessed
     * @param fieldName the name of the field being accessed
     *
     * @throws SecurityException if there is a {@link SecurityManager security
     * manager} and its {@link
     * SecurityManager#checkPermission(java.security.Permission)} method returns
     * <code>true</code> for the
     * <code>ReflectPermission("suppressAccessChecks")</code> permission. Note
     * that this is an unchecked exception.
     *
     * @pre class != null && obj != null && fieldName != null
     **/
    public static Object getPrivateField(Class klass, Object obj,
                                         String fieldName)
        throws SecurityException {

        Assert.assertNotNull(klass, "klass");
        Assert.assertNotNull(obj, "obj");
        Assert.assertNotNull(fieldName, "fieldName");

        try {
            Field field = klass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException
                ("No field named " + fieldName + " in " + klass.getName());
        } catch (IllegalAccessException ex) {
            throw new Error("This can't normally happen.");
        }
    }

    /**
     * Manipulates the logging level for the specified logger.
     *
     * <p>Possible use case: Suppose you want to log db queries generated as a
     * result of executing the method <code>foo()</code>.  One way to do this is
     * to set the level for the
     * <code>"com.arsdigita.db.PreparedStatement"</code> logger to
     * <code>"info"</code> in <code>enterprise.init</code>.  However, this will
     * result in <em>all</em> queries being logged, producing a flood of
     * debugging information that you have to wade through to find queries that
     * are of interest to you.</p>
     *
     * <p>An alternative is to do something like this: </p>
     *
     * <pre>
     *  String old = Debug.setLevel("com.arsdigita.db.PreparedStatement", "info");
     *  foo();
     *  Debug.setLevel("com.arsdigita.db.PreparedStatement", old);
     *  // or
     *  Debug.setLevel("com.arsdigita.db.PreparedStatement", "off");
     * </pre>
     * 
     * <p>Note, however, that although this method allows you to eliminate <em>a
     * lot</em> of unnecessary clutter, it fails to eliminate all of it. In the
     * presence of multiple threads of execution, methods other than
     * <code>foo()</code> may execute within the same time slice.  These other
     * methods executing concurrently may produce additional logging in the
     * <code>PreparedStatement</code> class.  </p>
     * 
     * @see Level
     * @see Logger
     *
     * @param loggerName the name of the logger, usually the name of a class
     * @param level the logger level; one of <code>"debug"</code>,
     * <code>"info"</code>, <code>"warn"</code>, <code>"error"</code>,
     * <code>"fatal"</code>, or <code>"off"</code>. If an invalid level name is
     * passed, the level will default to <code>"debug"</code>.
     * @return the previous level prior to this call
     **/
    public static String setLevel(String loggerName, String level) {
        Logger logger = Logger.getLogger(loggerName);
        Level old = logger.getLevel();
        logger.setLevel(Level.toLevel(level));
        return old == null ? null : old.toString();
    }
}
