package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * JoinThrough
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class JoinThrough extends Mapping {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/metadata/JoinThrough.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private ForeignKey m_from;
    private ForeignKey m_to;

    public JoinThrough(Path path, ForeignKey from, ForeignKey to) {
        super(path);
        m_from = from;
        m_to = to;
    }

    public Table getTable() {
        return m_from.getUniqueKey().getTable();
    }

    public ForeignKey getFrom() {
        return m_from;
    }

    public ForeignKey getTo() {
        return m_to;
    }

    public void dispatch(Switch sw) {
        sw.onJoinThrough(this);
    }

}
