package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * Value
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/05 $
 **/

public class Value extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Value.java#1 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

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
