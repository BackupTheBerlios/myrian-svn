package com.redhat.persistence.engine.rdbms;

/**
 * RDBMSProfiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public interface RDBMSProfiler {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/RDBMSProfiler.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    StatementLifecycle getLifecycle(RDBMSStatement stmt);

}
