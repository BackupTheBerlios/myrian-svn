/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence.metadata;
import java.math.BigInteger;

/**
 * SmallIntBigInteger
 *
 *  This is an implementation of BigInteger for values <= 32 bits. This is
 *  because Integer.toString(int) is signifigantly faster than BigInteger.toSring().
 *  This class is intended to be used within MetadataRoot when instantiating
 *  values from the database.
 *
 * @version $Revision: #8 $ $Date: 2004/04/07 $
 *
 */
class SmallIntBigInteger extends BigInteger {
    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/SmallIntBigInteger.java#8 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    SmallIntBigInteger(byte[] val) {
        super(val);
        validate();
    }

    private void validate() {
        if( bitLength() >= 32 ) {
            throw new IllegalArgumentException("Value " + super.toString() + " too large!");
        }

    }

    public String toString() {
        return Integer.toString(intValue());
    }

}
