/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.redhat.persistence.pdl.nodes;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class PropertyNd extends StatementNd {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/nodes/PropertyNd.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public static final Field TYPE =
        new Field(PropertyNd.class, "type", TypeNd.class, 1, 1);
    public static final Field NAME =
        new Field(PropertyNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field MAPPING =
        new Field(PropertyNd.class, "mapping", Node.class, 0, 1);
    public static final Field NESTED_MAP =
        new Field(NestedMapNd.class, "nestedMap", NestedMapNd.class, 0, 1);

    private boolean m_isImmediate = false;
    private boolean m_isUnique = false;
    private boolean m_isComponent = false;
    private boolean m_isComposite = false;
    private Integer m_lower = null;
    private Integer m_upper = new Integer(1);

    private boolean m_isUnversioned = false;
    private boolean m_isVersioned = false;

    /**
     * @post isImmediate()
     **/
    public void setImmediate() {
        m_isImmediate = true;
    }

    public boolean isImmediate() {
        return m_isImmediate;
    }

    /**
     * @post isUnique()
     **/
    public void setUnique() {
        m_isUnique = true;
    }

    /**
     * @post isComponent()
     **/
    public void setComponent() {
        m_isComponent = true;
    }

    /**
     * @post isComposite()
     **/
    public void setComposite() {
        m_isComposite = true;
    }

    public Integer getLower() {
        return m_lower;
    }

    public void setLower(Integer lower) {
        m_lower = lower;
    }

    public Integer getUpper() {
        return m_upper;
    }

    public void setUpper(Integer upper) {
        m_upper = upper;
    }

    /**
     * @see #isVersioned()
     * @post isVersioned()
     **/
    public void setVersioned() {
        m_isVersioned = true;
    }

    /**
     * @see #isUnversioned()
     * @post isUnversioned()
     **/
    public void setUnversioned() {
        m_isUnversioned = true;
    }


    public boolean isUnique() {
        return m_isUnique;
    }

    public boolean isComponent() {
        return m_isComponent;
    }

    public boolean isComposite() {
        return m_isComposite;
    }

    public boolean isCollection() {
        return m_upper == null;
    }

    public boolean isNullable() {
        return m_lower == null || m_lower.intValue() == 0;
    }

    /**
     * Returns <code>true</code> if this property is marked "versioned" in the
     * PDL.
     *
     * @see #isUnversioned()
     * @see #setVersioned()
     **/
    public boolean isVersioned() {
        return m_isVersioned;
    }

    /**
     * Returns <code>true</code> if this property is marked "unversioned" in the PDL.
     * 
     * <p>Note that <code>isUnversioned()</code> is <em>not</em> the same as
     * <code>!isVersioned()</code>. If the property is marked neither
     * "versioned", nor "unversioned" (as most properties are), then both of
     * these methods return <code>false</code>. However, if one of them returns
     * <code>true</code>, then other returns <code>false</code>. </p>
     *
     * @see #isVersioned()
     * @see #setVersioned()
     **/
    public boolean isUnversioned() {
        return m_isUnversioned;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onProperty(this);
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public TypeNd getType() {
        return (TypeNd) get(TYPE);
    }

    public Node getMapping() {
        return (Node) get(MAPPING);
    }
}
