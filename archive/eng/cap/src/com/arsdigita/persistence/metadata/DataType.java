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


/**
 * The DataType class represents the type of a persistently stored datum.
 * There are two flavors of DataTypes. SimpleTypes, and CompoundTypes.
 * CompoundTypes are defined in terms of other DataTypes both simple and
 * compound. Any value returned by the persistence layer must have an
 * associated DataType object.
 *
 * @see SimpleType
 * @see CompoundType
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 */

abstract public class DataType extends ModelElement {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/DataType.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    private com.redhat.persistence.metadata.ObjectType m_type;

    /**
     * Constructs a new DataType with the given name.
     *
     * @param name The semantically meaningful name of the datatype.
     **/

    protected DataType
	(com.redhat.persistence.metadata.ObjectType obj) {
        super(obj.getRoot(), obj.getModel(), obj);
	m_type = obj;
    }

    /**
     * Returns the semantically meaningful name of this datatype.
     *
     * @return the semantically meaningful name of this datatype.
     **/

    public String getName() {
	return m_type.getName();
    }


    /**
     * Returns the fully qualified name of this DataType. The fully qualified
     * name consists of the model name followed by a "." followed by the name
     * of this DataType.
     *
     * @return The fully qualified name of this DataType.
     **/

    public String getQualifiedName() {
	return m_type.getQualifiedName();
    }


    /**
     * Returns true if this DataType is a compound type, false otherwise.
     * Compound types are composed of other data types both simple and
     * compound.
     *
     * @return True if this DataType is a compound type. False otherwise.
     **/

    public boolean isCompound() {
	return m_type.isCompound();
    }

    /**
     * Returns true if this DataType is a simple type, false otherwise. Simple
     * types are not composed of other data types. They are the basic atoms
     * from which compound types can be created.
     *
     * @return True if this DataType is a simple type. False otherwise.
     **/

    public boolean isSimple() {
        return !isCompound();
    }

}
