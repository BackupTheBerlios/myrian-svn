package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * JoinTo
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/05 $
 **/

public class JoinTo extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/JoinTo.java#1 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

    private ForeignKey m_key;

    public JoinTo(Path path, ForeignKey key) {
        super(path);
        m_key = key;
    }

    public Table getTable() {
        return m_key.getColumns()[0].getTable();
    }

    public ForeignKey getKey() {
        return m_key;
    }

    public void dispatch(Switch sw) {
        sw.onJoinTo(this);
    }

}
