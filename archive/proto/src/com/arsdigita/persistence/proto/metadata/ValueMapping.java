package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * ValueMapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/17 $
 **/

public class ValueMapping extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ValueMapping.java#3 $ by $Author: rhs $, $DateTime: 2003/01/17 11:07:02 $";

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

}
