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
package com.redhat.persistence.metadata;

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.Session;
import com.redhat.persistence.common.CompoundKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Adapter.
 *
 * Subclasses must provide a public no-args constructor.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/30 $
 **/

public abstract class Adapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Adapter.java#7 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Root m_root;

    void setRoot(Root root) {
        m_root = root;
    }

    public Root getRoot() {
        return m_root;
    }

    public Object getSessionKey(PropertyMap props) {
        ObjectType type = props.getObjectType();
        Collection keys = type.getKeyProperties();
        Object key = type.getBasetype();
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Property p = (Property) it.next();
            Object value = props.get(p);
            if (p.getType().isKeyed() && value != null) {
                Adapter ad = getRoot().getAdapter(value.getClass());
                value = ad.getSessionKey(ad.getProperties(value));
            }
            key = new CompoundKey(key, value);
        }
        return key;
    }

    // This needs work. It's odd to have an adapter interface here in the
    // session layer that knows about prepared statements. Also I don't like
    // having things that throw unsupported operation exception.

    public Object fetch(ResultSet rs, String column) throws SQLException {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
        throws SQLException {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public int defaultJDBCType() {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public boolean isMutation(Object value, int jdbcType) {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public void mutate(ResultSet rs, String column, Object obj, int type)
        throws SQLException {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public Object getObject(ObjectType basetype,
                            PropertyMap props,
                            Session ssn) {

        throw new UnsupportedOperationException
            ("not a compound adapter: " + getClass().getName());
    }

    public abstract PropertyMap getProperties(Object obj);

    /**
     * @pre obj != null
     * @throws NullPointerException if <code>obj</code> is null.
     **/
    public abstract ObjectType getObjectType(Object obj);

}
