/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.util.parameter;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * A collection to store <code>ParameterError</code>s that are
 * encountered during parameter reading or validation.  This
 * collection is used in the lifecycle methods of
 * <code>Parameter</code>.  It is ordinarily returned to the
 * parameter-using code so that it can handle errors.
 *
 * @see ParameterError
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ErrorList.java#4 $
 */
public final class ErrorList {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ErrorList.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

    private static final Logger s_log = Logger.getLogger(ErrorList.class);

    private final ArrayList m_params;

    // XXX temporarily package access
    final ArrayList m_errors;

    /**
     * Constructs a new error list.
     */
    public ErrorList() {
        m_params = new ArrayList();
        m_errors = new ArrayList();
    }

    /**
     * Adds <code>error</code> to the error list.
     *
     * @param error A <code>ParameterError</code> representing a read
     * or validation error; it cannot be null
     */
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

    /**
     * Gets an iterator over the currently stored errors.
     *
     * @see ParameterError
     * @return An <code>Iterator</code> of
     * <code>ParameterError</code>s; it cannot be null
     */
    public final Iterator iterator() {
        return m_errors.iterator();
    }

    /**
     * Tells whether the error collection is empty or not.
     *
     * @return <code>true</code> if the collection is empty, otherwise
     * <code>false</code>
     */
    public final boolean isEmpty() {
        return m_errors.isEmpty();
    }

    /**
     * Throws a <code>ParameterException</code> containing the error
     * list.  This method is for use when the client code wants the
     * program to fail via an exception if there are errors.
     *
     * @throws ParameterException if the error list is not empty
     */
    public final void check() throws ParameterException {
        if (!isEmpty()) {
            final StringWriter writer = new StringWriter();
            report(writer);
            s_log.error(writer.toString());

            throw new ParameterException
                ("Errors encountered while reading parameters", this);
        }
    }

    /**
     * Prints parameter errors to <code>out</code> with formatting
     * appropriate to the console.
     *
     * @param out The <code>Writer</code> to print the errors to
     */
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
