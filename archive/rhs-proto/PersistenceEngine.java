package com.arsdigita.persistence.proto;

/**
 * PersistenceEngine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

public interface PersistenceEngine {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PersistenceEngine.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 18:23:04 $";

    void commit();

    void rollback();

    RecordSet execute(Query query);

    void write(Event event);

    void flush();

    FilterSource getFilterSource();

    EventSource getEventSource();

}
