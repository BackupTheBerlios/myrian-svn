package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * Value
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class Value extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/Value.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Column m_column;

    public Value(Path path, Column column) {
        super(path);
        m_column = column;
    }

    public Table getTable() {
        return m_column.getTable();
    }

    public Column getColumn() {
        return m_column;
    }

    public void dispatch(Switch sw) {
        sw.onValue(this);
    }

}
