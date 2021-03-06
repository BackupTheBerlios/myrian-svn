/*
 * Copyright (C) 2001-2004 Red Hat, Inc. All Rights Reserved.
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
package com.arsdigita.persistence.metadata;


/**
 * The SimpleType class is the base class for all the primative DataTypes
 * that the persistence layer knows how to store. These simple types serve as
 * the atoms from which compound types are built.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 */

public class SimpleType extends DataType {


    static final SimpleType
	wrap(org.myrian.persistence.metadata.ObjectType obj) {
	if (obj == null) {
	    return null;
	} else {
	    return new SimpleType(obj);
	}
    }

    private org.myrian.persistence.metadata.ObjectType m_type;

    /**
     * Constructs a new SimpleType with the given name.
     **/

    private SimpleType
	(org.myrian.persistence.metadata.ObjectType obj) {
        super(obj);
	m_type = obj;
    }

    public Class getJavaClass() {
	return m_type.getJavaClass();
    }

}
