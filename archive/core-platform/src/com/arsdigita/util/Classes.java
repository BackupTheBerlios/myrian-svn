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
package com.arsdigita.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * A collection of static utility methods for dealing with Java
 * classes.
 *
 * @author Justin Ross
 */
public final class Classes {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/Classes.java#3 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

    private static final Logger s_log = Logger.getLogger(Classes.class);

    /**
     * Loads a class from its fully qualified string name.
     *
     * @param clacc A fully qualified <code>String</code> naming
     * the class to be loaded
     */
    public static final Class loadClass(final String clacc) {
        Assert.exists(clacc, String.class);

        try {
            return Class.forName(clacc);
        } catch (ClassNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    /**
     * Constructs a new instance of a class using the given
     * parameters.
     *
     * @param clacc The <code>Class</code> of which to make a new
     * instance
     * @param params A <code>Class[]</code> representing the arguments
     * of the desired constructor
     * @param values An <code>Object[]</code> of values to fill the
     * parameters
     */
    public static final Object newInstance(final Class clacc,
                                           final Class[] params,
                                           final Object[] values) {
        if (Assert.isEnabled()) {
            Assert.exists(clacc, Class.class);
            Assert.exists(params, Class.class);
            Assert.exists(values, Object.class);
            Assert.truth(params.length == values.length);
        }

        try {
            final Constructor constructor = clacc.getConstructor(params);

            return constructor.newInstance(values);
        } catch (NoSuchMethodException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        } catch (IllegalAccessException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        } catch (InvocationTargetException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        } catch (InstantiationException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        }
    }

    private static String message(Class klass, Class[] params,
                                  Object[] values) {
        return "class = " + klass +
            ", params = " + message(params) +
            ", values = " + message(values);
    }

    private static String message(Object[] array) {
        if (array == null) {
            return "" + null;
        } else {
            return Arrays.asList(array).toString();
        }
    }

    /**
     * Constructs a new instance of the class referred to by
     * <code>clacc</code>.
     *
     * @param clacc The fully qualified <code>String</code>
     * clacc of the object you wish to instantiate
     * @param params A <code>Class[]</code> representing the arguments
     * of the desired constructor
     * @param values An <code>Object[]</code> of values to fill the
     * parameters
     */
    public static final Object newInstance(final String clacc,
                                           final Class[] params,
                                           final Object[] values) {
        return newInstance(loadClass(clacc), params, values);
    }

    /**
     * Creates a new instance of <code>clacc</code> using its no-args
     * constructor.  If the class has no such constructor, it throws a
     * runtime exception.
     *
     * @param clacc The class of which to create a new instance
     */
    public static final Object newInstance(final Class clacc) {
        return newInstance(clacc, new Class[0], new Object[0]);
    }

    /**
     * Creates a new instance of the class represented by
     * <code>clacc</code> using its no-args constructor.  If the class
     * has no such constructor, it throws a runtime exception.
     *
     * @param clacc The fully-qualified <code>String</code> name of
     * the class
     */
    public static final Object newInstance(final String clacc) {
        return newInstance(loadClass(clacc));
    }
}
