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

/**
 * ForeignKey
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public class ForeignKey extends Constraint {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/ForeignKey.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private UniqueKey m_key;
    private boolean m_cascade;

    public ForeignKey(Table table, String name, Column[] columns,
                      UniqueKey key, boolean cascade) {
        super(table, name, columns);
        if (key == null) {
            throw new IllegalArgumentException(
                                               "Unique key cannot be null."
                                               );
        }
        m_key = key;
        m_cascade = cascade;

        Column[] fk = getColumns();
        Column[] uk = m_key.getColumns();
        if (fk.length != uk.length) {
            throw new IllegalArgumentException(
                                               "Foreign columns don't match unique key: fk = " + getSQL()
                                               + " uk = " + key.getSQL()
                                               );
        }

        for (int i = 0; i < fk.length; i++) {
            if (fk[i].getType() == Integer.MIN_VALUE) {
                fk[i].setType(uk[i].getType());
                fk[i].setSize(uk[i].getSize());
            } else {
                if (fk[i].getType() != uk[i].getType() &&
                    fk[i].getSize() != uk[i].getSize()) {
                    throw new IllegalArgumentException(
                                                       "Foreign columns don't match unique key."
                                                       );
                }
            }
        }

        m_key.addForeignKey(this);
    }

    public ForeignKey(Table table, String name, Column[] columns,
                      UniqueKey key) {
        this(table, name, columns, key, false);
    }

    public ForeignKey(String name, Column from, Column to, boolean cascade) {
        this(from.getTable(), name, new Column[] {from},
             to.getTable().getUniqueKey(new Column[] {to}), cascade);
    }

    public ForeignKey(String name, Column from, Column to) {
        this(name, from, to, false);
    }

    public UniqueKey getUniqueKey() {
        return m_key;
    }

    public boolean isNullable() {
        Column[] cols = getColumns();
        for (int i = 0; i < cols.length; i++) {
            if (!cols[i].isNullable()) {
                return false;
            }
        }
        return true;
    }

    public boolean isDeferred() {
        return true;
        //return getTable().isCircular() &&
        //m_key.getTable().isCircular();
    }

    String getSuffix() {
        return "_f";
    }

    String getColumnSQL() {
        StringBuffer result = new StringBuffer();

        result.append("        ");

        if (getName() != null) {
            result.append("constraint " + getName() + "\n          ");
        }

        result.append("references ");
        result.append(m_key.getTable().getName());
        result.append(m_key.getColumnList());

        if (m_cascade) {
            result.append(" on delete cascade");
        }

        return result.toString();
    }

    public String getSQL() {
        StringBuffer result = new StringBuffer();

        result.append("    ");

        if (getName() != null) {
            result.append("constraint " + getName() + " ");
        }

        result.append("foreign key ");
        result.append(getColumnList());
        result.append("\n      references ");
        result.append(m_key.getTable().getName());
        result.append(m_key.getColumnList());

        if (m_cascade) {
            result.append(" on delete cascade");
        }

        return result.toString();
    }

}
