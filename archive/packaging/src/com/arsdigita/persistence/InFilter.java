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

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.arsdigita.db.DbHelper;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

class InFilter extends FilterImpl implements Filter {

    private static Logger s_log = Logger.getLogger(InFilter.class);

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/InFilter.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

    private String m_prop;
    private String m_subProp;
    private String m_query;

    InFilter(String property, String subqueryProperty, String query) {
        m_prop = property;
        m_subProp = subqueryProperty;
        m_query = query;
        if (s_log.isDebugEnabled()) {
            s_log.debug("InFilter: " + property + " - " + subqueryProperty + " - " + query);
        }
    }

    private SQLBlock getBlock(String query) {
	Root root = Root.getRoot();
	ObjectType ot = root.getObjectType(query);
	return root.getObjectMap(ot).getRetrieveAll();
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
	    throw new MetadataException(block, "no such path: " + subProp);
	}

        String col;
        switch (DbHelper.getDatabase()) {
        case DbHelper.DB_ORACLE:
            col = subcol.getPath().toUpperCase();
            break;
        case DbHelper.DB_POSTGRES:
            col = subcol.getPath().toLowerCase();
            break;
        default:
            DbHelper.unsupportedDatabaseError("in filter");
            col = null;
            break;
        }

	return "exists ( select \"subquery_id\" from (select \"" +
            col + "\" as \"subquery_id\" from (" +
            m_query + ") \"insub1\" ) \"insub2\" where " +
            "\"insub2\".\"subquery_id\" = " + m_prop + ")";
    }

}
