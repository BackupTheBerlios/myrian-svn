/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * The Association class is used to link together the properties of two object
 * types. When such a link is made there can be data stored along with each
 * link.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/04/18 $
 **/

public class Association extends ModelElement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/Association.java#3 $ by $Author: rhs $, $DateTime: 2003/04/18 15:09:07 $";

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
