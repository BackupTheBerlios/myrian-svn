package com.arsdigita.persistence.proto;

/**
 * Order
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public class Order {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Order.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    private String m_path;
    private boolean m_isAscending;

    public Order(String path, boolean isAscending) {
        m_path = path;
        m_isAscending = isAscending;
    }

}
