package com.arsdigita.persistence.sql;

import java.util.List;

/**
 * Assign
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 **/

public class Assign extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Assign.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private Identifier m_lhs;
    private SQL m_rhs;

    public Assign(Identifier lhs, SQL rhs) {
        m_lhs = lhs;
        m_rhs = rhs;
    }

    public Identifier getLHS() {
        return m_lhs;
    }

    public SQL getRHS() {
        return m_rhs;
    }

    public boolean isLeaf() {
        return false;
    }

    public void addLeafElements(List l) {
        m_lhs.addLeafElements(l);
        l.add(Symbol.getInstance("="));
        m_rhs.addLeafElements(l);
    }

    void makeString(SQLWriter result, Transformer tran) {
        m_lhs.output(result, tran);
        result.print(" = ");
        m_rhs.output(result, tran);
    }

}
