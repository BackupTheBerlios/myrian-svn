package com.arsdigita.persistence.proto;

import java.util.*;

/**
 * PropertyCursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

class PropertyCursor extends Cursor {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PropertyCursor.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    private PropertyData m_pdata;
    private ArrayList m_rows = null;
    private int m_position = -1;

    public PropertyCursor(PropertyData pdata) {
        m_pdata = pdata;
    }

    public boolean next() {
        if (m_rows == null) {
            m_rows = getRows();
        }

        return ++m_position < m_rows.size();
    }

    public Object getValue() {
        return m_rows.get(m_position);
    }

    private ArrayList getRows() {
        ArrayList result = new ArrayList();
        for (int i = 0; i < m_pdata.m_events.size(); i++) {
            PropertyEvent ev = (PropertyEvent) m_pdata.m_events.get(i);
            if (ev instanceof AddEvent) {
                result.add(ev.getArgument());
            } else if (ev instanceof RemoveEvent) {
                result.remove(ev.getArgument());
            }
        }

        return result;
    }

}
