/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.persistence.metadata;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * SmallIntBigDecimal    (Copyright 2001 ArsDigita Corporation)
 * 
 *  This is an implementation of BigDecimal for values <= 32 bits. This is
 *  because Integer.toString(int) is signifigantly faster than BigInteger.toSring().  
 *  All BigDecimals in Persistence are really integer values, so the conversion is
 *  valid.
 *  
 *  This class is intended to be used within MetadataRoot when instantiating
 *  values from the database.
 *
 * @author <a href="mailto:jorris@arsdigita.com">jorris@arsdigita.com</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
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
