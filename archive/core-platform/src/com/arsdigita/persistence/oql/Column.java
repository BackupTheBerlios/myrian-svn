package com.arsdigita.persistence.oql;

import java.util.Set;
import java.util.HashSet;

/**
 * Column
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/07/18 $
 **/

class Column {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Column.java#5 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private Table m_table;
    private String m_name;
    private Set m_sources = new HashSet();

    public Column(Table table, String name) {
        m_table = table;
        m_name = name;

        m_table.addColumn(this);
    }

    public void remove() {
        m_table.removeColumn(this);
    }

    public Table getTable() {
        return m_table;
    }

    String getFullName() {
        return m_table.getName() + "." + getName();
    }

    public String getName() {
        return m_name;
    }

    public String getQualifiedName() {
        return m_table.getAlias() + "." + m_name;
    }

    public String toString() {
        return getQualifiedName();
    }

    Set getSources() {
        return m_sources;
    }

}
