package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;

import java.util.*;

/**
 * RDBMSStatement
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/19 $
 **/

public class RDBMSStatement {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/RDBMSStatement.java#1 $ by $Author: rhs $, $DateTime: 2003/07/19 18:06:57 $";

    private final String m_text;
    private final ArrayList m_events = new ArrayList();
    private Query m_query = null;

    RDBMSStatement(String text) {
        m_text = text;
    }

    public String getText() {
        return m_text;
    }

    void setQuery(Query query) {
        m_query = query;
    }

    public Query getQuery() {
        return m_query;
    }

    void addEvent(Event ev) {
        if (m_events.contains(ev)) {
            throw new IllegalArgumentException
                ("Already contains event: " + ev);
        }

        m_events.add(ev);
    }

    public Collection getEvents() {
        return Collections.unmodifiableCollection(m_events);
    }

}
