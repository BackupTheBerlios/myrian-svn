/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.log4j.Logger;

/**
 * A utility class for notifying the developer of the state of a Java
 * object.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/proto/src/com/arsdigita/util/Reporter.java#4 $
 */
public final class Reporter {
    public static final String versionId =
        "$Id: //core-platform/proto/src/com/arsdigita/util/Reporter.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/08/04 16:15:53 $";

    private static final Logger s_log = Logger.getLogger(Reporter.class);

    private final Logger m_log;
    private final Object m_object;
    private BeanInfo m_info;

    public Reporter(final Logger log, final Object object) {
        this(log, object, object.getClass());
    }

    public Reporter(final Logger log,
                    final Object object,
                    final Class base) {
        Assert.exists(log, Logger.class);
        Assert.exists(object, Object.class);

        m_log = log;
        m_object = object;

        if (m_log.isDebugEnabled()) {
            final Class special = m_object.getClass();
            final Class general = base.getSuperclass();

            try {
                if (general == null) {
                    m_info = Introspector.getBeanInfo(special);
                } else {
                    m_info = Introspector.getBeanInfo(special, general);
                }
            } catch (IntrospectionException ie) {
                throw new UncheckedWrapperException(ie);
            }
        }
    }

    public final void mutated(final String property) {
        if (m_log.isDebugEnabled()) {
            Assert.exists(property, String.class);

            final PropertyDescriptor[] props = m_info.getPropertyDescriptors();

            for (int i = 0; i < props.length; i++) {
                final PropertyDescriptor prop = props[i];

                if (prop.getName().equals(property)) {
                    final Method method = prop.getReadMethod();

                    if (method != null) {
                        m_log.debug
                            (property + " set to " +
                             literal(value(method)) + " on " +
                             m_object);
                    }

                    break;
                }
            }
        }
    }

    public final void report() {
        if (m_log.isDebugEnabled()) {
            final PropertyDescriptor[] props = m_info.getPropertyDescriptors();

            m_log.debug("-*- " + m_object + " -*-");

            for (int i = 0; i < props.length; i++) {
                final Method method = props[i].getReadMethod();

                if (method != null) {
                    m_log.debug(print(method));
                }
            }
        }
    }

    private String print(final Method method) {
        final Class clacc = method.getReturnType();
        final Package pakkage = clacc.getPackage();
        final StringBuffer buffer = new StringBuffer(64);

        if (pakkage == null) {
            buffer.append(clacc.getName());
        } else {
            buffer.append
                (clacc.getName().substring(pakkage.getName().length() + 1));
        }

        buffer.append(" ");
        buffer.append(method.getName());

        final int len = 30 - buffer.length();

        if (len > 0) {
            final char[] spacer = new char[len];

            Arrays.fill(spacer, ' ');

            buffer.insert(0, spacer);
        }

        buffer.append("() -> ");
        buffer.append(literal(value(method)));

        return buffer.toString();
    }

    private String literal(final Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return "\"" + object + "\"";
        } else {
            return object.toString();
        }
    }

    private Object value(final Method method) {
        try {
            return method.invoke(m_object, new Object[] {});
        } catch (IllegalAccessException iae) {
            throw new UncheckedWrapperException(iae);
        } catch (InvocationTargetException ite) {
            throw new UncheckedWrapperException(ite);
        }
    }
}
