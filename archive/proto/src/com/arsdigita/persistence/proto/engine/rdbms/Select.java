package com.arsdigita.persistence.proto.engine.rdbms;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/30 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Select.java#1 $ by $Author: rhs $, $DateTime: 2003/01/30 17:57:25 $";

    private String m_sql;

    public Select(String sql) {
        super(null, null);
        m_sql = sql;
    }

    public String toString() {
        return m_sql;
    }

}
