/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.MetadataException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2003/10/28 $
 **/

class InFilter extends FilterImpl implements Filter {

    private static Logger s_log = Logger.getLogger(InFilter.class);

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/InFilter.java#7 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private Root m_root;
    private String m_prop;
    private String m_subProp;
    private String m_query;

    InFilter(Root root, String property, String subqueryProperty,
             String query) {
        m_root = root;
        m_prop = property;
        m_subProp = subqueryProperty;
        m_query = query;
        if (s_log.isDebugEnabled()) {
            s_log.debug("InFilter: " + property + " - " + subqueryProperty +
                        " - " + query);
        }
    }

    private SQLBlock getBlock(String query) {
        ObjectType ot = m_root.getObjectType(query);
        ObjectMap map = m_root.getObjectMap(ot);
        if (map == null) {
            throw new PersistenceException("no such query: " + query);
        }
        return map.getRetrieveAll();
    }

    public String getConditions() {
        SQLBlock block = getBlock(m_query);
        Path subProp;
        if (m_subProp == null) {
            Iterator paths = block.getPaths().iterator();
            if (paths.hasNext()) {
                subProp = (Path) paths.next();
            } else {
                return m_prop + " in (" + m_query + ")";
            }

            if (paths.hasNext()) {
                throw new PersistenceException
                    ("subquery has more than one mapping");
            }
        } else {
            subProp = Path.get(m_subProp);
        }

        Path subcol = block.getMapping(subProp);
        if (subcol == null) {
            throw new MetadataException
                (m_root, block, "no such path: " + subProp);
        }

        return "exists ( select RAW[subquery_id] from (select RAW[" +
            subcol.getPath() + "] as RAW[subquery_id] from (" +
            m_query + ") RAW[insub1] ) RAW[insub2] where " +
            "RAW[insub2.subquery_id] = " + m_prop + ")";
    }

}
