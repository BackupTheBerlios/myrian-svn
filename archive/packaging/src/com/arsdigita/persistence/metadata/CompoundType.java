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


import java.util.*;

/**
 * The CompoundType class represents types that are built up from SimpleTypes
 * and other CompoundTypes. A CompoundType has a set of properties. Each
 * property contained in a CompoundType has an associated DataType.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 */

abstract public class CompoundType extends DataType {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/metadata/CompoundType.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";


    /**
     * Constructs a new and empty CompoundType with the given name. In order
     * to do anything useful with the type you must add any properties it may
     * have.
     *
     * @param name The name of this compound type.
     **/

    public CompoundType
	(com.redhat.persistence.metadata.ObjectType obj) {
        super(obj);
    }


    /**
     * Gets a property that this CompoundType contains. Returns null if no
     * such property exists.
     *
     * @param name The name of the property.
     *
     * @return The property with name <i>name</i>, or null if no such property
     *         exists.
     **/

    public abstract Property getProperty(String name);


    /**
     * Returns true if and only if this CompoundType has a property with the
     * given name.
     *
     * @param name The name of the property for which to check existence.
     *
     * @return True if this CompoundType has a property with the given name.
     *         False otherwise.
     **/

    public abstract boolean hasProperty(String name);


    /**
     * Returns an iterator containing all the Properties this CompoundType
     * contains.
     *
     * @return An iterator containing all the Properties this CompoundType
     *         contains.
     *
     * @see Property
     **/

    public abstract Iterator getProperties();


    /**
     * This method will always return true. It is the implementation of the
     * abstract method that appears in DataType.
     *
     * @return true
     **/

    public boolean isCompound() {
        return true;
    }

}
