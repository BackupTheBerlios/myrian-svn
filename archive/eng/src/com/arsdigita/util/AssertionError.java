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

/**
 * Assertion failure.
 *
 * @see Assert#fail(String)
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since  2003-05-21
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/
public class AssertionError extends Error {

    public AssertionError(String msg) {
        super(msg);
    }
}
