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
import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/Record.java#4 $
 */
public abstract class Record {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/Record.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/08/15 13:46:34 $";

    private static final Logger s_log = Logger.getLogger(Record.class);

    private Class m_class;
    private Logger m_log;
    private String[] m_fields;
    private boolean m_undergoingAccess = false;

    protected Record(Class clacc, Logger log, String[] fields) {
        m_class = clacc;
        m_fields = fields;
        m_log = log;
    }

    protected final void accessed(String field) {
        if (m_log.isDebugEnabled()) {
            synchronized (this) {
                if (m_undergoingAccess == false) {
                    final Method accessor = accessor(field);

                    m_undergoingAccess = true;
                    final String value = prettyLiteral(value(accessor));
                    m_undergoingAccess = false;

                    m_log.debug("Returning " + value + " for " + field);
                }
            }
        }
    }

    protected final void mutated(String field) {
        if (m_log.isInfoEnabled()) {
            final Method accessor = accessor(field);

            m_undergoingAccess = true;
            final String value = prettyLiteral(value(accessor));
            m_undergoingAccess = false;

            m_log.info(field + " set to " + value);
        }
    }

    private String prettyLiteral(final Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return "\"" + o + "\"";
        } else {
            return o.toString();
        }
    }

    private Method accessor(final String field) {
        try {
            Method method = m_class.getDeclaredMethod
                ("get" + field, new Class[] {});

            return method;
        } catch (NoSuchMethodException nsme) {
            try {
                Method method = m_class.getDeclaredMethod
                    ("is" + field, new Class[] {});

                return method;
            } catch (NoSuchMethodException me) {
                throw new UncheckedWrapperException(nsme);
            }
        }
    }

    private Object value(final Method m) {
        try {
            return m.invoke(this, new Object[] {});
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }

    public final String getCurrentState() {
        final StringBuffer info = new StringBuffer();

        for (int i = 0; i < m_fields.length; i++) {
            final Method method = accessor(m_fields[i]);
            final String name = method.getName();
            final String value = prettyLiteral(value(method));
            final int len = name.length();

            if (len < 30) {
                for (int j = 0; j < 30 - len; j++) {
                    info.append(' ');
                }
            }

            info.append(name);
            info.append("() -> ");
            info.append(value);
            info.append("\n");
        }

        return info.toString();
    }
}
