package com.arsdigita.persistence;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/04/25 $
 **/

class InFilter extends FilterImpl implements Filter {

    private static Logger s_log = Logger.getLogger(InFilter.class);

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/InFilter.java#3 $ by $Author: rhs $, $DateTime: 2003/04/25 15:49:01 $";

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

	return "exists ( select \"subquery_id\" from (select \"" +
            subcol.getPath() + "\" as \"subquery_id\" from (" +
            m_query + ") \"insub1\" ) \"insub2\" where " +
            "\"insub2\".\"subquery_id\" = " + m_prop + ")";
    }

}