/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.util.xml;

import org.apache.log4j.Logger;

public class XML {
    
    private static final Logger s_log = Logger.getLogger
        (XMLConfig.class);

    private static XMLConfig s_config;
    static {
        s_config = new XMLConfig();
        s_config.load();
    }
    
    static XMLConfig getConfig() {
        return s_config;
    }

    public static void setupFactories() {
        setupFactory("javax.xml.parsers.DocumentBuilderFactory",
                     getConfig().getDOMBuilderFactory());
        setupFactory("javax.xml.parsers.SAXParserFactory",
                     getConfig().getSAXParserFactory());
        setupFactory("javax.xml.transform.TransformerFactory",
                     getConfig().getXSLTransformerFactory());
    }
    
    static void setupFactory(String name,
                             Class impl) {
        if (impl != null) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Setting " + name + " to " + impl);
            }
            System.setProperty(name,
                               impl.getName());
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("Leaving " + name + " as " +
                           System.getProperty(name));
            }
        }
    }
}
