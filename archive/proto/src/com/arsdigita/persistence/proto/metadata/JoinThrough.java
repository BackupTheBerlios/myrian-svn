package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

/**
 * JoinThrough
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/05 $
 **/

public class JoinThrough extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/JoinThrough.java#1 $ by $Author: rhs $, $DateTime: 2003/03/05 18:41:57 $";

    private ForeignKey m_from;
    private ForeignKey m_to;

    public JoinThrough(Path path, ForeignKey from, ForeignKey to) {
        super(path);
        m_from = from;
        m_to = to;
    }

    public Table getTable() {
        return m_from.getUniqueKey().getColumns()[0].getTable();
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
