/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Category;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;

/**
 * The Element class is the abstract base class for functionality common to
 * all metadata classes.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/08/06 $
 */

abstract public class Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Element.java#5 $ by $Author: rhs $, $DateTime: 2002/08/06 16:54:58 $";

    private static final Category s_log =
        Category.getInstance(Element.class.getName());

    /**
     * This is for backwards compatibility.
     **/

    static Object caseInsensativeGet(Map map, String key) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String entryKey = (String) me.getKey();
            if (key.equalsIgnoreCase(entryKey)) {
                Throwable t = new Throwable();
                StringWriter str = new StringWriter();
                PrintWriter msg = new PrintWriter(str);
                msg.println(
                    "Warning: The key named '" + key + "' was not actually " +
                    "found in the metadata."
                    );
                msg.println(
                    "For backwards compatibility we are assuming you mean " +
                    " this: '" + entryKey + "'. Please fix this bug ASAP."
                    );
                msg.println(
                    "Here is the stack trace so you can figure out where " +
                    "this bug is occuring:"
                    );
                t.printStackTrace(msg);
                s_log.warn(str.toString());
                return me.getValue();
            }
        }

        return null;
    }


    // Once these are properly initialized we'll be able to improve runtime
    // error reporting.
    private String m_filename = "<unknown>";
    private int m_line = -1;
    private int m_column = -1;

    // Stores any options that this metadata object may have.
    private Map m_options = new HashMap();


    /**
     * Sets the filename for this metadata element.
     **/

    public void setFilename(String filename) {
        m_filename = filename;
    }


    /**
     * Returns the filename for this metadata element.
     **/

    public String getFilename() {
        return m_filename;
    }


    /**
     * Returns the line number for this metadata element.
     **/

    public int getLineNumber() {
        return m_line;
    }


    /**
     * Returns the column number for this metadata element.
     **/

    public int getColumnNumber() {
        return m_column;
    }

    public void error(String msg) {
        throw new Error(
            m_filename + ": " + m_line + " column " + m_column + ": " + msg
            );
    }

    /**
     * Sets the line number info for this metadata element.
     **/

    public void setLineInfo(int line, int column) {
        m_line = line;
        m_column = column;
    }

    public void setLineInfo(Element el) {
        m_filename = el.m_filename;
        m_line = el.m_line;
        m_column = el.m_column;
    }

    public String getLocation() {
        return m_filename + ": " + m_line + ", column " + m_column;
    }

    /**
     * Sets the value for the option with the given name.
     **/

    public void setOption(String name, Object value) {
        if (m_options.containsKey(name)) {
            m_options.put(name, value);
        } else {
            throw new IllegalArgumentException(
                "No such option: " + name
                );
        }
    }


    /**
     * Returns the value for the option with the given name.
     **/

    public Object getOption(String name) {
        if (m_options.containsKey(name)) {
            return m_options.get(name);
        } else {
            throw new IllegalArgumentException("No such name: " + name);
        }
    }


    /**
     * Initialize the given option with the given default value.
     **/

    void initOption(String name, Object defaultValue) {
        if (m_options.containsKey(name)) {
            throw new IllegalStateException("Option initialized twice: " + name);
        } else {
            m_options.put(name, defaultValue);
        }
    }

    /**
     *  Outputs the PDL for the element options in a block similar to
     * <code><pre>
     * options {
     *    option1 = option1Value;
     *    .....
     * }
     * </pre></code>
     * 
     */
    public void outputOptionsPDL(PrintStream out) {
        if (m_options.size() > 0) {
            out.println("    options {");
            for (Iterator it = m_options.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                String entryKey = (String) me.getKey();
                out.println("        " + entryKey + " = " + 
                            m_options.get(entryKey) + ";");
            }                
            out.println("    }");
        }
    }


    /**
     * Returns true if this type has an option with the given name.
     **/

    public boolean hasOption(String name) {
        return m_options.containsKey(name);
    }


    /**
     * Outputs a serialized representation of the metadata element using the
     * passed in PrintStream.
     *
     * @param out The PrintStream to use for output.
     **/

    abstract void outputPDL(PrintStream out);

    public String toString() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        outputPDL(new PrintStream(stream));
        return stream.toString();
    }

}
