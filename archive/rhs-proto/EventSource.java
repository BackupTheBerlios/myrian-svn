package com.arsdigita.persistence.proto;

/**
 * EventSource
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public interface EventSource {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/EventSource.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    public CreateEvent getCreate(ObjectData odata);

    public DeleteEvent getDelete(ObjectData odata);

    public SetEvent getSet(PropertyData pdata, Object argument);

    public AddEvent getAdd(PropertyData pdata, Object argument);

    public RemoveEvent getRemove(PropertyData pdata, Object argument);

}
