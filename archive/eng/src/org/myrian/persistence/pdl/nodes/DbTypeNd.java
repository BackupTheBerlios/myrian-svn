/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
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
package org.myrian.persistence.pdl.nodes;


/**
 * DbType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class DbTypeNd extends Node {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/pdl/nodes/DbTypeNd.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

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
            throw new Error(e);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        } catch (IllegalAccessException e) {
            throw new Error(e);
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
