package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * Value
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class Value extends Mapping {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/Value.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

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
