package com.arsdigita.persistence.proto;

/**
 * PersistenceEngine
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/12/02 $
 **/

public interface PersistenceEngine {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PersistenceEngine.java#1 $ by $Author: rhs $, $DateTime: 2002/12/02 12:04:21 $";

    void commit();

    void rollback();

    RecordSet execute(Query query);

    void write(Event event);

    void flush();

    FilterSource getFilterSource();

    EventSource getEventSource();

}
