/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.db;

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
 * @version $Revision: #1 $
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
