package com.redhat.persistence.oql;

/**
 * Exists
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/02/09 $
 **/

public class Exists extends UnaryCondition {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Exists.java#6 $ by $Author: ashah $, $DateTime: 2004/02/09 16:16:05 $";

    public Exists(Expression query) {
        super(query);
    }

    void emit(Code code) {
        Code.Frame frame = code.getFrame(m_operand);
        code.append("exists (select 1 from ");
        m_operand.emit(code);
        code.append(" where ");
        String[] columns = frame.getColumns();
        for (int i = 0; i < columns.length; i++) {
            code.append(columns[i] + " is not null");
            if (i < columns.length - 1) {
                code.append(" and ");
            }
        }
        code.append(")");
    }

    public String toString() {
        return "exists(" + m_operand + ")";
    }

    String summary() { return "exists"; }

}
