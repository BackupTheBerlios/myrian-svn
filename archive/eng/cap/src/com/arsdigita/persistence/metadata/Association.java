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

import com.redhat.persistence.common.CompoundKey;

/**
 * The Association class is used to link together the properties of two object
 * types. When such a link is made there can be data stored along with each
 * link.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Association extends ModelElement {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/Association.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Property m_roleOne;
    private Property m_roleTwo;

    Association(Property p1, Property p2) {
	super(p1.m_prop.getRoot(), null,
	      new CompoundKey(first(p1, p2), second(p1, p2)));
	m_roleOne = first(p1, p2);
	m_roleTwo = second(p1, p2);
    }

    private static final Property first(Property p1, Property p2) {
	String s1 = p1.getContainer().getQualifiedName() + ":" + p1.getName();
	String s2 = p2.getContainer().getQualifiedName() + ":" + p2.getName();
	if (s1.compareTo(s2) < 0) {
	    return p1;
	} else {
	    return p2;
	}
    }

    private static final Property second(Property p1, Property p2) {
	String s1 = p1.getContainer().getQualifiedName() + ":" + p1.getName();
	String s2 = p2.getContainer().getQualifiedName() + ":" + p2.getName();
	if (s1.compareTo(s2) < 0) {
	    return p2;
	} else {
	    return p1;
	}
    }

    /**
     * Gets the DataType to be used as a link in this Association.
     *
     * @return The DataType to be used as a link.
     **/

    public CompoundType getLinkType() {
        return m_roleOne.getLinkType();
    }

    /**
     * Gets the associated property.
     **/

    public Property getAssociatedProperty(Property prop) {
        if (prop.equals(m_roleOne)) {
	    return m_roleTwo;
	} else if (prop.equals(m_roleTwo)) {
	    return m_roleOne;
	} else {
	    throw new IllegalArgumentException
		("property not in association: " + prop);
	}
    }

    /**
     * Gets the first role property.
     *
     * @return the first role property
     */
    public Property getRoleOne() {
        return m_roleOne;
    }
    /**
     * Gets the second role property.
     *
     * @return the second role property
     */
    public Property getRoleTwo() {
        return m_roleTwo;
    }

}
