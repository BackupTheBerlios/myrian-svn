package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * JoinTo
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class JoinTo extends Mapping {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/metadata/JoinTo.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private ForeignKey m_key;

    public JoinTo(Path path, ForeignKey key) {
        super(path);
        m_key = key;
    }

    public Table getTable() {
        return m_key.getTable();
    }

    public ForeignKey getKey() {
        return m_key;
    }

    public void dispatch(Switch sw) {
        sw.onJoinTo(this);
    }

}
