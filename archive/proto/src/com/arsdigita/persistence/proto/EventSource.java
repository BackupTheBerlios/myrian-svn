package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;

/**
 * EventSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public interface EventSource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/EventSource.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    public CreateEvent getCreate(Session ssn, OID oid);

    public DeleteEvent getDelete(Session ssn, OID oid);

    public SetEvent getSet(Session ssn, OID oid, Property prop,
                           Object argument);

    public AddEvent getAdd(Session ssn, OID oid, Property prop,
                           Object argument);

    public RemoveEvent getRemove(Session ssn, OID oid, Property prop,
                                 Object argument);

}
