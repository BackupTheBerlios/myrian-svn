package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * DataOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class DataOperation extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/DataOperation.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Path m_name;
    private SQLBlock m_sql;

    public DataOperation(Path name, SQLBlock sql) {
        m_name = name;
        m_sql = sql;
    }

    public Path getName() {
        return m_name;
    }

    public SQLBlock getSQL() {
        return m_sql;
    }

    Object getElementKey() {
        return m_name;
    }

    public String toString() {
        return "data operation " + getName() + " {\n" + getSQL() + " }\n";
    }

}
