package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Identifier
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class IdentifierNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/IdentifierNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private String m_name;

    public IdentifierNd(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public String toString() {
        return m_name;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onIdentifier(this);
    }

}
