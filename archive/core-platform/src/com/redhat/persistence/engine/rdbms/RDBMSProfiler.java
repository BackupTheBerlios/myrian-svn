package com.redhat.persistence.engine.rdbms;

/**
 * RDBMSProfiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/19 $
 **/

public interface RDBMSProfiler {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/RDBMSProfiler.java#1 $ by $Author: rhs $, $DateTime: 2003/07/19 18:06:57 $";

    StatementLifecycle getLifecycle(RDBMSStatement stmt);

    void rollback();
    void commit();

}
