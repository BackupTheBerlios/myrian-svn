/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.xml;

import org.myrian.util.Assert;

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

    private XML() {}

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
            throw new RuntimeException("error parsing stream", e);
        } catch (SAXException e) {
            if (e.getException() != null) {
                throw new RuntimeException("error parsing stream",
                                                    e.getException());
            } else {
                throw new RuntimeException("error parsing stream", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("error parsing stream", e);
        }
    }

}
