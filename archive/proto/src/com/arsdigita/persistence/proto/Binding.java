package com.arsdigita.persistence.proto;

import java.util.*;

/**
 * Binding
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/06 $
 **/

public class Binding {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Binding.java#1 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

    private Query m_query;
    private HashMap m_values;

    public Binding(Query query) {
        m_query = query;
        m_values = new HashMap(query.getParameters().size());
    }

    public Query getQuery() {
        return m_query;
    }

    public void set(Parameter p, Object value) {
        m_values.put(p, value);
    }

    public Object get(Parameter p) {
        return m_values.get(p);
    }

    public String toString() {
        return "Query: " + m_query + "\nBindings: " + m_values;
    }

}
