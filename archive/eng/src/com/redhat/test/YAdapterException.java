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
package com.redhat.test;

/**
 * Note that this is an unchecked exception.
 *
 * @since 2004-05-21
 * @author Vadim Nasardinov (vadimn@redhat.com)
 **/
public class YAdapterException extends RuntimeException {
    public YAdapterException(Throwable cause) {
        super(cause);
    }

    public YAdapterException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public YAdapterException(String msg) {
        super(msg);
    }
}
