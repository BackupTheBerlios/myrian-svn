/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.xml;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Assert;

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
