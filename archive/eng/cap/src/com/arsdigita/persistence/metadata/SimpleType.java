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


/**
 * The SimpleType class is the base class for all the primative DataTypes
 * that the persistence layer knows how to store. These simple types serve as
 * the atoms from which compound types are built.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

public class SimpleType extends DataType {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/SimpleType.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    static final SimpleType
	wrap(com.redhat.persistence.metadata.ObjectType obj) {
	if (obj == null) {
	    return null;
	} else {
	    return new SimpleType(obj);
	}
    }

    private com.redhat.persistence.metadata.ObjectType m_type;

    /**
     * Constructs a new SimpleType with the given name.
     **/

    private SimpleType
	(com.redhat.persistence.metadata.ObjectType obj) {
        super(obj);
	m_type = obj;
    }

    public Class getJavaClass() {
	return m_type.getJavaClass();
    }

}
