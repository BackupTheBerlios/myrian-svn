package com.arsdigita.persistence.proto.metadata;


/**
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

public class Join {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Join.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private Column m_from;
    private Column m_to;

    public Join(Column from, Column to) {
        m_from = from;
        m_to = to;
    }

    public Column getFrom() {
        return m_from;
    }

    public Column getTo() {
        return m_to;
    }

}
