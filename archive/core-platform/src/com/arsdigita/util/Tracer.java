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

package com.arsdigita.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/Tracer.java#2 $
 */
public final class Tracer {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/Tracer.java#2 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/08/15 13:46:34 $";

    private static final Logger s_log = Logger.getLogger(Tracer.class);

    private Logger m_log;
    private int m_level;
    private HashMap m_starts;

    public Tracer(final Logger log) {
        m_log = log;
        m_level = 0;
        m_starts = new HashMap();
    }

    public Tracer(final String category) {
        this(Logger.getLogger(category));
    }

    public Tracer(final Class clacc) {
        this(clacc.getName() + ".trace");
    }

    public final boolean isEnabled() {
        return m_log.isDebugEnabled();
    }

    public final void enter(final String method) {
        if (isEnabled()) enter(method, new Object[] {});
    }

    public final void enter(final String method,
                            final Object arg1) {
        if (isEnabled()) enter(method, new Object[] {arg1});
    }

    public final void enter(final String method,
                            final Object arg1,
                            final Object arg2) {
        if (isEnabled()) enter(method, new Object[] {arg1, arg2});
    }

    public final void enter(final String method,
                            final Object arg1,
                            final Object arg2,
                            final Object arg3) {
        if (isEnabled()) enter(method, new Object[] {arg1, arg2, arg3});
    }

    public final void enter(final String method, final Object[] args) {
        if (isEnabled()) {
            m_level++;

            final StringBuffer buffer = buffer();

            buffer.append(method);

            m_starts.put(buffer.toString(),
                         new Long(System.currentTimeMillis()));

            buffer.append(" ");
            buffer.append(Arrays.asList(args));

            m_log.debug(buffer.toString());
        }
    }

    public final void exit(final String method) {
        if (isEnabled()) exit(method, null);
    }

    public final void exit(final String method, final Object result) {
        if (isEnabled()) {
            final StringBuffer buffer = buffer();

            buffer.append(method);

            final long start = ((Long) m_starts.get
                                    (buffer.toString())).longValue();
            final long end = System.currentTimeMillis();

            if (result != null) {
                buffer.append(" -> ");
                buffer.append(result);
            }

            buffer.append(" (");
            buffer.append((end - start));
            buffer.append(" millis)");

            m_log.debug(buffer.toString());

            m_level--;
        }
    }

    private StringBuffer buffer() {
        final StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < m_level; i++) {
            buffer.append("  ");
        }

        return buffer;
    }
}
