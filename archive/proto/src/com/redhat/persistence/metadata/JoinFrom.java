package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * JoinFrom
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class JoinFrom extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/JoinFrom.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private ForeignKey m_key;

    public JoinFrom(Path path, ForeignKey key) {
        super(path);
        m_key = key;
    }

    public Table getTable() {
        return m_key.getUniqueKey().getTable();
    }

    public ForeignKey getKey() {
        return m_key;
    }

    public void dispatch(Switch sw) {
        sw.onJoinFrom(this);
    }

}
