package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * DataOperation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class DataOperation extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/metadata/DataOperation.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
