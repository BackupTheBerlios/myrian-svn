package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * ValueMapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/01/28 $
 **/

public class ValueMapping extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ValueMapping.java#4 $ by $Author: rhs $, $DateTime: 2003/01/28 19:17:39 $";

    private Column m_column;

    public ValueMapping(Path path, Column column) {
        super(path);
        m_column = column;
    }

    public Column getColumn() {
        return m_column;
    }

    public void dispatch(Switch sw) {
        sw.onValue(this);
    }

    public boolean isValue() {
        return true;
    }

    public boolean isReference() {
        return false;
    }

}
