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
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * This won't extend ArrayList for long.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ErrorList.java#2 $
 */
//public final class ErrorList extends ArrayList {
public final class ErrorList {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ErrorList.java#2 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/21 17:54:40 $";

    private static final Logger s_log = Logger.getLogger(ErrorList.class);

    private final ArrayList m_params;

    // XXX temporarily package access
    final ArrayList m_errors;

    public ErrorList() {
        m_params = new ArrayList();
        m_errors = new ArrayList();
    }

    public final void add(final ParameterError error) {
        Assert.exists(error, ParameterError.class);

        final Parameter param = error.getParameter();

        synchronized (m_params) {
            if (!m_params.contains(param)) {
                m_params.add(param);
            }
        }

        m_errors.add(error);
    }

    public final Iterator iterator() {
        return m_errors.iterator();
    }

    public final boolean isEmpty() {
        return m_errors.isEmpty();
    }

    public final void check() throws ParameterException {
        if (!isEmpty()) {
            final StringWriter writer = new StringWriter();
            report(writer);
            s_log.error(writer.toString());

            throw new ParameterException
                ("Errors encountered while reading parameters", this);
        }
    }

    public final void report(final Writer out) {
        try {
            Assert.exists(out, PrintWriter.class);

            final Iterator params = m_params.iterator();

            while (params.hasNext()) {
                final Parameter param = (Parameter) params.next();

                out.write("Parameter " + param.getName() + " has the " +
                          "following errors:\n");

                final Iterator errors = m_errors.iterator();

                while (errors.hasNext()) {
                    final ParameterError error =
                        (ParameterError) errors.next();

                    if (error.getParameter().equals(param)) {
                        out.write("\t" + error.getMessage() + "\n");
                    }
                }
            }

            out.flush();
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }
}
