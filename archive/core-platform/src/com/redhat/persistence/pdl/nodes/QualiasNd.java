package com.redhat.persistence.pdl.nodes;

/**
 * QualiasNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public class QualiasNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/QualiasNd.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

    private String m_query;

    public QualiasNd(String query) {
        m_query = query;
    }

    public String getQuery() {
        return m_query;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onQualias(this);
    }

}
