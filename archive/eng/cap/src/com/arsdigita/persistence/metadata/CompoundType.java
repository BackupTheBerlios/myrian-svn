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


import java.util.Iterator;

/**
 * The CompoundType class represents types that are built up from SimpleTypes
 * and other CompoundTypes. A CompoundType has a set of properties. Each
 * property contained in a CompoundType has an associated DataType.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

abstract public class CompoundType extends DataType {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/CompoundType.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";


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
