/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence.pdl.nodes;

/**
 * DbType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class DbTypeNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/pdl/nodes/DbTypeNd.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public static final Field NAME =
        new Field(DbTypeNd.class, "name", IdentifierNd.class, 1, 1);

    private int m_size = -1;
    private int m_scale = -1;

    public void setSize(int size) {
        m_size = size;
    }

    public void setScale(int scale) {
        m_scale = scale;
    }

    public int getSize() {
        return m_size;
    }

    public int getScale() {
        return m_scale;
    }

    public int getType() {
        String type = getName().getName();
        try {
            Class types = Class.forName("java.sql.Types");
            java.lang.reflect.Field f = types.getField(type);
            return f.getInt(null);
        } catch (ClassNotFoundException e) {
            throw new Error(e.getMessage());
        } catch (NoSuchFieldException e) {
            throw new Error(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new Error(e.getMessage());
        }
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }


    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onDbType(this);
    }

}
