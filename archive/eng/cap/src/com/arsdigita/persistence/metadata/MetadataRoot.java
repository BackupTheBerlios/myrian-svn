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

import com.redhat.persistence.metadata.Root;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.log4j.Logger;

/**
 * The MetadataRoot is a singleton class that serves as an entry point for the
 * metadata system.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class MetadataRoot extends Element {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/MetadataRoot.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private static final Logger s_cat = Logger.getLogger(MetadataRoot.class.getName());

    /**
     * The MetadataRoot instance for this JVM.
     **/
    private static MetadataRoot s_root;

    // the following is a list of constants that can be used to specify the
    // type
    public static SimpleType BIGINTEGER;
    public static SimpleType BIGDECIMAL;
    public static SimpleType BOOLEAN;
    public static SimpleType BYTE;
    public static SimpleType CHARACTER;
    public static SimpleType DATE;
    public static SimpleType DOUBLE;
    public static SimpleType FLOAT;
    public static SimpleType INTEGER;
    public static SimpleType LONG;
    public static SimpleType SHORT;
    public static SimpleType STRING;
    public static SimpleType BLOB;
    public static SimpleType CLOB;
    // This is for backword compatibility with data queries.
    public static SimpleType OBJECT;

    public static final void loadPrimitives() {
	BIGINTEGER = s_root.getPrimitiveType("BigInteger");
	BIGDECIMAL = s_root.getPrimitiveType("BigDecimal");
	BOOLEAN = s_root.getPrimitiveType("Boolean");
	BYTE = s_root.getPrimitiveType("Byte");
	CHARACTER = s_root.getPrimitiveType("Character");
	DATE = s_root.getPrimitiveType("Date");
	DOUBLE = s_root.getPrimitiveType("Double");
	FLOAT = s_root.getPrimitiveType("Float");
	INTEGER = s_root.getPrimitiveType("Integer");
	LONG = s_root.getPrimitiveType("Long");
	SHORT = s_root.getPrimitiveType("Short");
	STRING = s_root.getPrimitiveType("String");
	BLOB = s_root.getPrimitiveType("Blob");
	CLOB = s_root.getPrimitiveType("Clob");
	OBJECT = s_root.getPrimitiveType("Object");
    }


    public static final void clear() {
        throw new Error("not implemented");
    }


    private static final Map OLD_ROOTS = new WeakHashMap();

    public static final MetadataRoot getMetadataRoot(Root root) {
        return (MetadataRoot) OLD_ROOTS.get(root);
    }

    /**
     * Returns the MetadataRoot instance for this JVM.
     *
     * @return The MetadataRoot instance for this JVM.
     **/
    public static final MetadataRoot getMetadataRoot() {
        return s_root;
    }

    static {
        s_root = new MetadataRoot();
    }


    private final Root m_root;

    private MetadataRoot(Root root) {
	super(root, root);
	m_root = root;
        OLD_ROOTS.put(m_root, this);
    }

    public MetadataRoot() {
        this(new Root());
    }

    public final Root getRoot() {
	return m_root;
    }

    /**
     * Returns the Model with the specified name, or null if no such model
     * exists.
     *
     * @param name The name of the model to get.
     *
     * @return The specified Model, or null.
     **/

    public Model getModel(String name) {
        for (Iterator it = getObjectTypes().iterator(); it.hasNext(); ) {
	    ObjectType ot = (ObjectType) it.next();
	    if (ot.getModel().getName().equals(name)) {
		return ot.getModel();
	    }
	}

	return null;
    }


    /**
     * Returns true if a model with the given name exists as part of this
     * MetadataRoot.
     *
     * @return True if a model with the given name exists as part of this
     *         MetadataRoot.
     **/

    public boolean hasModel(String name) {
	return getModel(name) != null;
    }


    /**
     * Returns an Iterator of all the Models contained by this MetadataRoot.
     *
     * @return An Iterator containing instances of Model.
     *
     * @see Model
     **/

    public Iterator getModels() {
	HashSet result = new HashSet();
	for (Iterator it = getObjectTypes().iterator(); it.hasNext(); ) {
	    result.add(((ObjectType) it.next()).getModel());
	}
	return result.iterator();
    }

    /**
     * Returns an ObjectType given a fully qualified type name or null if no
     * such type exists. The fully qualified name consists of the model name,
     * followed by a '.' followed by the type name.
     *
     * @param name The fully qualified name of the ObjectType.
     *
     * @return The ObjectType or null.
     **/

    public ObjectType getObjectType(String name) {
	return ObjectType.wrap(m_root.getObjectType(name));
    }

    /**
     * Returns a collection of the object types in this metadata root
     *
     * @return a collection of the object types in this metadata root
     */
    public Collection getObjectTypes() {
	return ObjectType.wrap(m_root.getObjectTypes());
    }

    public Set getAssociations() {
	throw new Error("not implemented");
    }

    /**
     * Returns one of the primitive or predefined types.
     *
     * @param name The name of the primitive type.
     *
     * @return The type.
     **/

    public SimpleType getPrimitiveType(String name) {
        return SimpleType.wrap(m_root.getObjectType("global." + name));
    }

    public boolean hasTable(String name) {
        return m_root.hasTable(name);
    }

}
