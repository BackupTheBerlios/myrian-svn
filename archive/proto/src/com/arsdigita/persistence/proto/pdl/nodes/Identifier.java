package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Identifier
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Identifier extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Identifier.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private String m_name;

    public Identifier(String name) {
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
