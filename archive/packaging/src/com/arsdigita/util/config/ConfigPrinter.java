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

package com.arsdigita.util.config;

import com.arsdigita.util.*;
import com.arsdigita.util.parameter.*;
import com.arsdigita.templating.*;
import java.io.*;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/ConfigPrinter.java#1 $
 */
public class ConfigPrinter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/ConfigPrinter.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/19 02:38:52 $";

    private static final Logger s_log = Logger.getLogger
        (ConfigPrinter.class);

    private static final ArrayList s_configs = new ArrayList();

    static void register(final ConfigRecord config) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Registering " + config);
        }

        s_configs.add(config);
    }

    private static void writeXML(final PrintWriter out) {
        out.write("<?xml version=\"1.0\"?>");
        out.write("<records>");

        final Iterator configs = s_configs.iterator();

        while (configs.hasNext()) {
            ((ConfigRecord) configs.next()).writeXML(out);
        }

        out.write("</records>");
        out.close();
    }

    public static final void main(final String[] args) throws IOException {
        try {
            // XXX These are cheats to get the configs loaded.

            Classes.loadClass("com.arsdigita.util.Util");
            Classes.loadClass("com.arsdigita.init.Init");
            Classes.loadClass("com.arsdigita.templating.Templating");
            Classes.loadClass("com.arsdigita.versioning.Versioning");
            Classes.loadClass("com.arsdigita.web.BaseServlet");
            Classes.loadClass("com.arsdigita.bebop.Bebop");
        } catch (Exception e) {
            s_log.error(e.getMessage(), e);
        }

        if (args[0].equals("--html") && args.length == 2) {
            final StringWriter sout = new StringWriter();
            final PrintWriter out = new PrintWriter(sout);

            writeXML(out);

            final XSLTemplate template = new XSLTemplate
                (ConfigPrinter.class.getResource("ConfigPrinter.xsl.properties")); // XXX build system work around

            final Source source = new StreamSource
                (new StringReader(sout.toString()));
            final Result result = new StreamResult(new File(args[1]));

            template.transform(source, result);
        } else if (args.length == 1) {
            final PrintWriter out = new PrintWriter
                (new FileWriter(args[0]));

            writeXML(out);
        } else {
            System.out.println("Usage: command [--html] output-file");
        }
    }
}
