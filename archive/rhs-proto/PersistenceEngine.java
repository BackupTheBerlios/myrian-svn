package com.arsdigita.persistence.proto;

/**
 * PersistenceEngine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

public interface PersistenceEngine {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/PersistenceEngine.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    void commit();

    void rollback();

    Cursor execute(Query query);

    void write(Event event);

    void flush();

    FilterSource getFilterSource();

    EventSource getEventSource();

}
