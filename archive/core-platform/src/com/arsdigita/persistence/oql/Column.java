package com.arsdigita.persistence.oql;

/**
 * Column
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

class Column {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Column.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private Table m_table;
    private String m_name;

    public Column(Table table, String name) {
        m_table = table;
        m_name = name;

        m_table.addColumn(this);
    }

    public Table getTable() {
        return m_table;
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

}
