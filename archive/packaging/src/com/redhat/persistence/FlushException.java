package com.redhat.persistence;

import java.util.*;

/**
 * FlushException. It has an object for which a flush was attempted. If the
 * object is null, the associated flush was for all events. The constructors
 * take a collection of property datas which are responsible for the inability
 * to flush.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class FlushException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/FlushException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private final Object m_obj;

    private static String msg(Object obj, Collection pds) {
        StringBuffer sb = new StringBuffer();
        if (obj == null) {
            sb.append("Unable to send all events to database");
        } else {
            sb.append("Unable to send all events to database for object ");
            sb.append(obj);
        }
        sb.append(" because these required properties are null:");

        for (Iterator it = pds.iterator(); it.hasNext(); ) {
            PropertyData pd = (PropertyData) it.next();
            sb.append("\n ");
            sb.append(pd.getObjectData().getObject() + "."
                      + pd.getProperty().getName());
        }

        return sb.toString();
    }

    FlushException(Object obj, Collection pds) {
	super(msg(obj, pds), false);
        m_obj = obj;

        if (Session.LOG.isInfoEnabled()) {
            Session.LOG.info("Unable to send all events to database.", this);
        }
    }

    FlushException(Collection pds) {
        this(null, pds);
    }

    public Object getObject() { return m_obj; }

}
