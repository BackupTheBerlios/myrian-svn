package com.arsdigita.persistence.metadata;

import com.arsdigita.util.*;

import java.util.*;
import java.io.*;

/**
 * Table
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/08/06 $
 **/

public class Table {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Table.java#1 $ by $Author: rhs $, $DateTime: 2002/08/06 16:54:58 $";

    private String m_name;
    private Map m_columns = new HashMap();
    private Set m_constraints = new HashSet();
    private UniqueKey m_key = null;

    public Table(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setPrimaryKey(UniqueKey constraint) {
        m_key = constraint;
    }

    public UniqueKey getPrimaryKey() {
        return m_key;
    }

    Constraint getConstraint(Class type, Column[] columns) {
        Set cols = new HashSet();
        for (int i = 0; i < columns.length; i++) {
            cols.add(columns[i]);
        }

        Set key = new HashSet();
        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (type.isInstance(con)) {
                Column[] keyCols = con.getColumns();
                key.clear();
                for (int i = 0; i < keyCols.length; i++) {
                    key.add(keyCols[i]);
                }

                if (cols.equals(key)) {
                    return con;
                }
            }
        }

        return null;
    }

    public UniqueKey getUniqueKey(Column[] columns) {
        return (UniqueKey) getConstraint(UniqueKey.class, columns);
    }

    public UniqueKey getUniqueKey(Column column) {
        return getUniqueKey(new Column[] {column});
    }

    public ForeignKey getForeignKey(Column[] columns) {
        return (ForeignKey) getConstraint(ForeignKey.class, columns);
    }

    public ForeignKey getForeignKey(Column column) {
        return getForeignKey(new Column[] {column});
    }

    void addColumn(Column column) {
        Column old = (Column) m_columns.get(column.getName());
        if (old == null || old.getType() == Integer.MIN_VALUE) {
            m_columns.put(column.getName(), column);
        }
    }

    public Column getColumn(String name) {
        return (Column) m_columns.get(name);
    }

    void addConstraint(Constraint constraint) {
        m_constraints.add(constraint);
    }

    public Set getConstraints() {
        return m_constraints;
    }

    public String getSQL() {
        StringBuffer result = new StringBuffer();

        List columns = new ArrayList();
        columns.addAll(m_columns.keySet());
        Collections.sort(columns);

        for (int i = 0; i < columns.size(); i++) {
            String colName = (String) columns.get(i);
            Column col = getColumn(colName);
            if (col.hasPrimaryKey()) {
                columns.remove(i);
                columns.add(0, colName);
            }
        }

        result.append("create table " + getName() + " (\n");

        String comment = null;
        for (int i = 0; i < columns.size(); i++) {
            String colName = (String) columns.get(i);
            Column column = getColumn(colName);
            result.append(column.getInlineSQL());
            boolean hasNext = i < columns.size() - 1;
            if (hasNext) {
                result.append(",\n");
            }
            if (column.hasDefferedConstraints()) {
                comment = "        -- referential constraint for " +
                    column.getName() +
                    " deffered due to circular dependencies";
                if (hasNext) {
                    result.append(comment);
                    result.append("\n");
                    comment = null;
                }
            }
        }

        boolean compoundDeffered = false;

        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (con.getColumns().length > 1) {
                if (con.isDeffered()) {
                    compoundDeffered = true;
                } else {
                    if (comment != null) {
                        result.append(",\n");
                        result.append(comment);
                        result.append("\n");
                        comment = null;
                    } else {
                        result.append(",\n");
                    }
                    result.append(con.getSQL());
                }
            }
        }

        if (comment != null) {
            result.append("\n");
            result.append(comment);
            comment = null;
        }

        if (compoundDeffered) {
            result.append("\n    -- compound referential constraints " +
                          "deffered due to circular dependencies");
        }

        result.append("\n);");

        return result.toString();
    }

    public boolean isCircular() {
        return getAllDependencies().contains(this);
    }

    private Set getAllDependencies() {
        Set result = new HashSet();

        getDependencies(result, true);

        int before;

        do {
            before = result.size();
            List tables = new ArrayList();
            tables.addAll(result);

            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                table.getDependencies(result, true);
            }
        } while (result.size() > before);

        return result;
    }

    public Set getDependencies() {
        Set result = new HashSet();
        getDependencies(result, false);
        return result;
    }

    private void getDependencies(Set result, boolean includeDeffered) {
        for (Iterator it = m_constraints.iterator(); it.hasNext(); ) {
            Constraint con = (Constraint) it.next();
            if (!includeDeffered && con.isDeffered()) {
                continue;
            }
            if (con instanceof ForeignKey) {
                ForeignKey fk = (ForeignKey) con;
                Table table = fk.getUniqueKey().getTable();
                if (table != this) {
                    result.add(table);
                }
            }
        }
    }

    public String toString() {
        return m_name;
    }

}