/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.db;

import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Helps avoid a compile-time dependency on Oracle's
 * <code>oracle.sql.BLOB</code> and <code>oracle.sql.CLOB</code> classes.
 *
 * @since 2004-07-09
 * @version $Revision: #2 $
 * @author Vadim Nasardinov (vadimn@redhat.com)
 **/
public final class OraLob {
    private final static Object[] EMPTY_VARARGS = new Object[] {};

    private final static Method GET_BINARY_OUTPUT_STREAM;
    private final static Method GET_CHARACTER_OUTPUT_STREAM;

    static {
        Class[] emptySignature = new Class[] {};

        Class blobClass;
        Class clobClass;
        try {
            blobClass = Class.forName("oracle.sql.BLOB");
            clobClass = Class.forName("oracle.sql.CLOB");
        } catch (ClassNotFoundException ex) {
            throw (IllegalStateException)
                new IllegalStateException().initCause(ex);
        }
        try {
            GET_BINARY_OUTPUT_STREAM = blobClass.getDeclaredMethod
                ("getBinaryOutputStream", emptySignature);
            GET_CHARACTER_OUTPUT_STREAM = clobClass.getDeclaredMethod
                ("getCharacterOutputStream", emptySignature);
        } catch (NoSuchMethodException ex) {
            throw (IllegalStateException)
                new IllegalStateException().initCause(ex);
        }
    }

    private OraLob() {}

    /**
     * @param blob an instance of <code>oracle.sql.BLOB</code>
     **/
    public static OutputStream getBinaryOutputStream(Blob blob)
        throws SQLException {

        return (OutputStream)
            invoke(GET_BINARY_OUTPUT_STREAM, blob, EMPTY_VARARGS);

    }

    /**
     * @param clob an instance of <code>oracle.sql.CLOB</code>
     **/
    public static Writer getCharacterOutputStream(Clob clob)
        throws SQLException {

        return (Writer)
            invoke(GET_CHARACTER_OUTPUT_STREAM, clob, EMPTY_VARARGS);
    }

    private static Object invoke(Method method, Object receiver, Object[] args)
        throws SQLException {

        try {
            return method.invoke(receiver, args);
        } catch (IllegalAccessException ex) {
            throw (IllegalStateException)
                new IllegalStateException("can't happen").initCause(ex);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof SQLException) {
                throw (SQLException) ex.getCause();
            } else {
                throw (IllegalStateException)
                    new IllegalStateException().initCause(ex);
            }
        }
    }
}
