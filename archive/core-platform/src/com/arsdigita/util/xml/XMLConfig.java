/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.xml;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.AliasedClassParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.EnumerationParameter;

import org.apache.log4j.Logger;

/**
 */
public final class XMLConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger
        (XMLConfig.class);

    private final AliasedClassParameter m_xfmr;
    private final AliasedClassParameter m_builder;
    private final AliasedClassParameter m_parser;

    public XMLConfig() {
        m_xfmr = new AliasedClassParameter
            ("waf.xml.xsl_transformer",
             Parameter.OPTIONAL,
             null);

        m_xfmr.addAlias("jd.xslt", XSLTransformer.JD_XSLT);
        m_xfmr.addAlias("xsltc", XSLTransformer.XSLTC);
        m_xfmr.addAlias("xalan", XSLTransformer.XALAN);
        m_xfmr.addAlias("saxon", XSLTransformer.SAXON);
        m_xfmr.addAlias("resin", XSLTransformer.RESIN);



        m_builder = new AliasedClassParameter
            ("waf.xml.dom_builder",
             Parameter.OPTIONAL,
             null);

        m_builder.addAlias("xerces", DOMBuilder.XERCES);
        m_builder.addAlias("resin", DOMBuilder.RESIN);



        m_parser = new AliasedClassParameter
            ("waf.xml.sax_parser",
             Parameter.OPTIONAL,
             null);

        m_parser.addAlias("xerces", SAXParser.XERCES);
        m_parser.addAlias("resin", SAXParser.RESIN);

        register(m_xfmr);
        register(m_builder);
        register(m_parser);

        loadInfo();
    }

    public final Class getXSLTransformerFactory() {
        return (Class)get(m_xfmr);
    }

    public final Class getDOMBuilderFactory() {
        return (Class)get(m_builder);
    }

    public final Class getSAXParserFactory() {
        return (Class)get(m_parser);
    }

}
