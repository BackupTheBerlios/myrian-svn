/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.xml;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.ResourceManager;
import com.arsdigita.util.Assert;

import com.arsdigita.xml.formatters.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import org.apache.log4j.Logger;

/**
 * Provides a set of helper methods for dealing with XML,
 * including file parsing &amp; object -> string serialization
 */
public class XML {

    private static final Logger s_log = Logger.getLogger(XML.class);

    private static Map s_formatters = new HashMap();
    static {
        s_formatters.put(Date.class, new DateTimeFormatter());
    }

    private XML() {}

    /**
     * Registers a formatter for serializing objects of a
     * class to a String suitable for XML output.
     */
    public static void registerFormatter(Class klass,
                                         Formatter formatter) {
        s_formatters.put(klass, formatter);
    }

    /**
     * Unregisters a formatter against a class.
     */
    public static void unregisterFormatter(Class klass) {
        s_formatters.remove(klass);
    }

    /**
     * Gets a directly registered formatter for a class.
     * @param klass the class to find a formatter for
     * @return the formatter, or null if non is registered
     */
    public static Formatter getFormatter(Class klass) {
        return (Formatter)s_formatters.get(klass);
    }

    /**
     * Looks for the best matching formatter.
     * @param klass the class to find a formatter for
     * @return the formatter, or null if non is registered
     */
    public static Formatter findFormatter(Class klass) {
        Formatter formatter = null;
        while (formatter == null && klass != null) {
            formatter = getFormatter(klass);
            klass = klass.getSuperclass();
        }
        return formatter;
    }

    /**
     * Converts an object to a String using the closest
     * matching registered Formatter implementation. Looks
     * for a formatter registered against the object's
     * class first, then its superclass, etc. If no formatter
     * is found, uses the toString() method
     */
    public static String format(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String)value;
        }

        Formatter formatter = findFormatter(value.getClass());
        if (formatter == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No formatter for " + value.getClass());
            }
            return value.toString();
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing " + value.getClass() +
                        " with " + formatter.getClass());
        }
        return formatter.format(value);
    }

    /**
     * Processes an XML file with the default SAX Parser, with
     * namespace processing, schema validation & DTD validation
     * enabled.
     *
     * @param path the XML file relative to the webapp root
     * @param handler the content handler
     */
    public static final void parseResource(String path,
                                           DefaultHandler handler) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing resource " + path +
                        " with " + handler.getClass());
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        ClassLoader cload = Thread.currentThread().getContextClassLoader();
        InputStream stream = cload.getResourceAsStream(path);

        if (stream == null) {
            throw new IllegalArgumentException("no such resource: " + path);
        }

        parse(stream, handler);
    }

    /**
     * Processes an XML file with the default SAX Parser, with
     * namespace processing, schema validation & DTD validation
     * enabled.
     *
     * @param source the xml input stream
     * @param handler the content handler
     */
    public static final void parse(InputStream source,
                                   DefaultHandler handler) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing stream " + source +
                        " with " + handler.getClass());
        }

        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://xml.org/sax/features/namespaces", true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(source, handler);
        } catch (ParserConfigurationException e) {
            throw new UncheckedWrapperException("error parsing stream", e);
        } catch (SAXException e) {
            if (e.getException() != null) {
                throw new UncheckedWrapperException("error parsing stream",
                                                    e.getException());
            } else {
                throw new UncheckedWrapperException("error parsing stream", e);
            }
        } catch (IOException e) {
            throw new UncheckedWrapperException("error parsing stream", e);
        }
    }

    /**
     * This visitor is called by {@link #traverse(Element, int, XML.Action)}.
     **/
    public interface Action {
        void apply(Element elem, int level);
    }

    /**
     * Prints the skeleton structure of the element to the supplied print
     * writer.
     **/
    public static void toSkeleton(final Element element,
                                  final PrintWriter writer) {

        XML.traverse(element, 0, new Action() {
                public void apply(Element elem, int level) {
                    final String padding = "  ";
                    for (int ii=0; ii<level; ii++) {
                        writer.print(padding);
                    }
                    writer.print(elem.getName());
                    Iterator attrs = elem.getAttributes().keySet().iterator();
                    while (attrs.hasNext()) {
                        writer.print(" @");
                        writer.print((String) attrs.next());
                    }
                    writer.println("");
                }
            });
    }

    /**
     * This is a wrapper for {@link #toSkeleton(Element, PrintWriter)}.
     **/
    public static String toSkeleton(Element element) {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        XML.toSkeleton(element, pw);
        pw.close();
        return writer.toString();
    }

    /**
     * Pre-order, depth-first traversal.
     **/
    public static void traverse(Element elem, int level, Action action) {
        action.apply(elem, level);
        final Iterator children=elem.getChildren().iterator();
        while (children.hasNext()) {
            XML.traverse((Element) children.next(), level+1, action);
        }
    }
}
