/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 * @version $Revision: #6 $ $Date: 2004/08/05 $
 **/

public abstract class Adapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Adapter.java#6 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

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
