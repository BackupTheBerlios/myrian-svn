package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * JoinTo
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class JoinTo extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/JoinTo.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
