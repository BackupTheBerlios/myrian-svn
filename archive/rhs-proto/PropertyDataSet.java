package com.arsdigita.persistence.proto;

/**
 * PropertyDataSet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

class PropertyDataSet extends DataSet {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PropertyDataSet.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    private PropertyData m_pdata;

    PropertyDataSet(PropertyData pdata) {
        m_pdata = pdata;
    }

    public Cursor getCursor() {
        return new PropertyCursor(m_pdata);
    }

}
