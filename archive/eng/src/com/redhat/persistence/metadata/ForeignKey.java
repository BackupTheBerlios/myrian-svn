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

/**
 * ForeignKey
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class ForeignKey extends Constraint {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/ForeignKey.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
