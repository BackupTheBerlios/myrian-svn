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

package com.arsdigita.util.parameter;

import com.arsdigita.util.*;
import com.arsdigita.templating.*; // XXX arh, dependency
import java.io.*;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterPrinter.java#2 $
 */
final class ParameterPrinter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterPrinter.java#2 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/09 10:25:57 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterPrinter.class);

    private static final ArrayList s_records = new ArrayList();

    private static void writeXML(final PrintWriter out) {
        out.write("<?xml version=\"1.0\"?>");
        out.write("<records>");

        final Iterator records = s_records.iterator();

        while (records.hasNext()) {
            ((ParameterRecord) records.next()).writeXML(out);
        }

        out.write("</records>");
        out.close();
    }

    private static void register(final String classname) {
        s_records.add((ParameterRecord) Classes.newInstance(classname));
    }

    public static final void main(final String[] args) throws IOException {
        register("com.arsdigita.runtime.RuntimeConfig");
        register("com.arsdigita.templating.TemplatingConfig");
        register("com.arsdigita.mail.MailConfig");
        register("com.arsdigita.versioning.VersioningConfig");
        register("com.arsdigita.web.WebConfig");
        register("com.arsdigita.bebop.BebopConfig");

        if (args.length == 0) {
            System.out.println("Usage: ParameterPrinter [--html] output-file");
        } else if (args[0].equals("--html") && args.length == 2) {
            final StringWriter sout = new StringWriter();
            final PrintWriter out = new PrintWriter(sout);

            writeXML(out);

            final XSLTemplate template = new XSLTemplate
                (ParameterPrinter.class.getResource("ParameterPrinter.xsl.properties")); // XXX build system work around

            final Source source = new StreamSource
                (new StringReader(sout.toString()));
            final Result result = new StreamResult(new File(args[1]));

            template.transform(source, result);
        } else if (args.length == 1) {
            final PrintWriter out = new PrintWriter
                (new FileWriter(args[0]));

            writeXML(out);
        } else {
            System.out.println("Usage: ParameterPrinter [--html] output-file");
        }
    }
}
