package com.arsdigita.persistence;

import com.arsdigita.util.*;
import com.arsdigita.persistence.metadata.*;
import java.util.*;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/19 $
 **/

class InFilter extends FilterImpl implements Filter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/InFilter.java#1 $ by $Author: rhs $, $DateTime: 2003/03/19 18:16:01 $";

    private String m_prop;
    private String m_subProp;
    private String m_query;

    InFilter(String property, String subqueryProperty, String query) {
	m_prop = property;
	m_subProp = subqueryProperty;
	m_query = query;
    }

    private Operation getOperation(String query) {
	QueryType qt = MetadataRoot.getMetadataRoot().getQueryType(query);
	Event ev = qt.getEvent();
	return (Operation) ev.getOperations().next();
    }

    private String getColumn(String property, Operation op) {
	String[] path = StringUtils.split(property, '.');
	Mapping m = op.getMapping(path);
	if (m != null) {
	    return m.getColumn();
	} else {
	    throw new IllegalArgumentException
		("no such property (" + property + ") in operation: " + op);
	}
    }

    public String getSQL(DataQuery query) {
	String col = getColumn(m_prop, ((DataQueryImpl) query).getOperation());
	Operation op = getOperation(m_query);

	String subProp;
	if (m_subProp == null) {
	    Iterator mappings = op.getMappings();
	    if (mappings.hasNext()) {
		subProp = StringUtils.join
		    (((Mapping) mappings.next()).getPath(), '.');
	    } else {
		return mangleBindVars(col + " in (" + op.getSQL() + ")");
	    }

	    if (mappings.hasNext()) {
		throw new PersistenceException
		    ("subquery has more than one mapping");
	    }
	} else {
	    subProp = m_subProp;
	}

	String subcol = getColumn(subProp, op);

	String sql = "exists ( select " + subcol +
	    " from (" + op.getSQL() + ") insub where insub." + subcol +
	    " = " + col + ")";

	return mangleBindVars(sql);
    }

}
