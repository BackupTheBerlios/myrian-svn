package com.redhat.persistence.engine.rdbms;

/**
 * RDBMSProfiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public interface RDBMSProfiler {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/RDBMSProfiler.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    StatementLifecycle getLifecycle(RDBMSStatement stmt);

}
