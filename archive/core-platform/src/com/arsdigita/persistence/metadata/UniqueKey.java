package com.arsdigita.persistence.metadata;

import java.util.*;

/**
 * UniqueKey
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/12 $
 **/

public class UniqueKey extends Constraint {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/UniqueKey.java#3 $ by $Author: rhs $, $DateTime: 2002/08/12 16:19:25 $";

    private Column[] m_columns;
    private Set m_foreignKeys = new HashSet();

    public UniqueKey(Table table, String name, Column[] columns) {
        super(checkDuplicates(table, columns), name, columns);
    }

    private static final Table checkDuplicates(Table table,
                                               Column[] columns) {
        if (table.getUniqueKey(columns) != null) {
            throw new IllegalArgumentException(
                "Table already has a unique key: " + table.getName()
                );
        }
        return table;
    }

    public UniqueKey(String name, Column column) {
        this(column.getTable(), null, new Column[] {column});
    }

    public boolean isPrimaryKey() {
        return this.equals(getTable().getPrimaryKey());
    }

    public Set getForeignKeys() {
        return m_foreignKeys;
    }

    void addForeignKey(ForeignKey fk) {
        m_foreignKeys.add(fk);
    }

    boolean isDeferred() {
        return false;
    }

    String getSuffix() {
        if (isPrimaryKey()) {
            return "_pk";
        } else {
            return "_un";
        }
    }

    String getColumnSQL() {
        String keyword = isPrimaryKey() ? "primary key" : "unique";
        if (getName() == null) {
            return "        " + keyword;
        } else {
            return "        constraint " + getName() + "\n          " +
                keyword;
        }
    }

    String getSQL() {
        String keyword = isPrimaryKey() ? "primary key" : "unique";
        if (getName() == null) {
            return "    " + keyword + getColumnList(true);
        } else {
            return "    constraint " + getName() + "\n      " + keyword +
                getColumnList(true);
        }
    }

}
