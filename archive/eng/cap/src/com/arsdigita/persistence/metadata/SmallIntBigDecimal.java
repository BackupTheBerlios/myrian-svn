/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence.metadata;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * SmallIntBigDecimal
 *
 *  This is an implementation of BigDecimal for values <= 32 bits. This is
 *  because Integer.toString(int) is signifigantly faster than BigInteger.toSring().
 *  All BigDecimals in Persistence are really integer values, so the conversion is
 *  valid.
 *
 *  This class is intended to be used within MetadataRoot when instantiating
 *  values from the database.
 *
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 *
 */
class SmallIntBigDecimal extends BigDecimal {

    SmallIntBigDecimal(BigInteger val) {
        super(val);
        if( val.bitLength() >= 32 ) {
            throw new IllegalArgumentException("Value " + super.toString() + " too large!");
        }
    }

    public String toString() {
        return Integer.toString(toBigInteger().intValue());
    }
}
