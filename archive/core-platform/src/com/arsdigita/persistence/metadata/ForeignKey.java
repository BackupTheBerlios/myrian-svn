package com.arsdigita.persistence.metadata;

/**
 * ForeignKey
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/09 $
 **/

public class ForeignKey extends Constraint {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/ForeignKey.java#3 $ by $Author: rhs $, $DateTime: 2002/08/09 15:10:37 $";

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

    boolean isDeferred() {
        return true;
        //return getTable().isCircular() &&
        //m_key.getTable().isCircular();
    }

    String getSuffix() {
        return "_fk";
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

    String getSQL() {
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
