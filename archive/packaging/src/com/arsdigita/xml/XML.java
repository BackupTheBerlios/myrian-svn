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

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

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
        
        InputStream stream = ResourceManager.getInstance().getResourceAsStream(path);
        Assert.exists(stream, InputStream.class);
        
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

}
