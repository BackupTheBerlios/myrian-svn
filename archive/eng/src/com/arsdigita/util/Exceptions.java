/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.util;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Exceptions {

    private static Logger s_log = Logger.getLogger(Exceptions.class);

    private static Map s_unwrappers = new HashMap();

    public static Throwable[] unwrap(Throwable t) {
        Assert.exists(t, Throwable.class);

        List exceptions = new ArrayList();
        
        exceptions.add(t);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Trying to unwrap " + t.getClass());
        }

        Throwable current = t;

        for (;;) {
            Throwable inner = null;
            ExceptionUnwrapper unwrapper = findUnwrapper(current.getClass());

            if (unwrapper != null) {
                inner = unwrapper.unwrap(current);
            }
            
            if (inner == null) {
                Assert.exists(current, Throwable.class);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Returning exception " + current.getClass());
                }
                return (Throwable[])exceptions.toArray(
                    new Throwable[exceptions.size()]);
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("Inner exception is " + inner.getClass());
            }

            exceptions.add(inner);

            current = inner;
        }

        // Unreachable
        //throw new RuntimeException("this cannot happen");
    }

    
    public static void registerUnwrapper(Class exception,
                                         ExceptionUnwrapper unwrapper) {
        s_unwrappers.put(exception, unwrapper);
    }

    public static void unregisterUnwrapper(Class exception) {
        s_unwrappers.remove(exception);
    }

    public static ExceptionUnwrapper getUnwrapper(Class exception) {
        return (ExceptionUnwrapper)s_unwrappers.get(exception);
    }

    public static ExceptionUnwrapper findUnwrapper(Class exception) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Finding unwrapper for " + exception.getName());
        }

        Class current = exception;
        ExceptionUnwrapper unwrapper = null;
        while (unwrapper == null && 
               current != null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Trying class " + current.getName());
            }
            unwrapper = (ExceptionUnwrapper)s_unwrappers.get(current);
            current = current.getSuperclass();
        }
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Got unwrapper " + 
                        (unwrapper != null ? unwrapper.getClass() : null));
        }
        return unwrapper;
    }
}
