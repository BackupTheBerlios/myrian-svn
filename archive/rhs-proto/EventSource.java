package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;

/**
 * EventSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

public interface EventSource {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/EventSource.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    public CreateEvent getCreate(Session ssn, OID oid);

    public DeleteEvent getDelete(Session ssn, OID oid);

    public SetEvent getSet(Session ssn, OID oid, Property prop,
                           Object argument);

    public AddEvent getAdd(Session ssn, OID oid, Property prop,
                           Object argument);

    public RemoveEvent getRemove(Session ssn, OID oid, Property prop,
                                 Object argument);

}
