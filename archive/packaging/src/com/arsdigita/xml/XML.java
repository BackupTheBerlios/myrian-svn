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

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Provides a set of helper methods for dealing with XML files.
 */
public class XML {

    private static final Logger s_log = Logger.getLogger(XML.class);
    
    /**
     * Processes an XML file with the default SAX Parser, with
     * namespace processing, schema validation & DTD validation
     * enabled.
     *
     * @param path the XML file relative to the webapp root
     * @param handler the content handler
     */
    public static final void parseResource(String file,
                                           DefaultHandler handler) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Processing file " + file + " with " + handler.getClass());
        }

        String path = ResourceManager.getInstance().getServletContext().
            getRealPath(file);
        
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://xml.org/sax/features/namespaces", true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(path, handler);
        } catch (ParserConfigurationException e) { 
            throw new UncheckedWrapperException("error parsing " + path, e);
        } catch (SAXException e) { 
            throw new UncheckedWrapperException("error parsing " + path, e);
        } catch (IOException e) { 
            throw new UncheckedWrapperException("error parsing " + path, e);
        }
    }
}
