package com.redhat.persistence.pdl.nodes;

/**
 * Identifier
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class IdentifierNd extends Node {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/nodes/IdentifierNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
