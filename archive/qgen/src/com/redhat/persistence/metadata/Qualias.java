package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

/**
 * Qualias
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/02/06 $
 **/

public class Qualias extends Mapping {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/metadata/Qualias.java#1 $ by $Author: rhs $, $DateTime: 2004/02/06 15:43:04 $";

    private String m_query;

    public Qualias(Path path, String query) {
        super(path);
        m_query = query;
    }

    public String getQuery() {
        return m_query;
    }

    public Table getTable() {
        return null;
    }

    public void dispatch(Switch sw) {
        sw.onQualias(this);
    }

}
