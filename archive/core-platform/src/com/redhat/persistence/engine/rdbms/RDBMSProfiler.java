package com.redhat.persistence.engine.rdbms;

/**
 * RDBMSProfiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/07/19 $
 **/

public interface RDBMSProfiler {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/RDBMSProfiler.java#2 $ by $Author: rhs $, $DateTime: 2003/07/19 20:26:22 $";

    StatementLifecycle getLifecycle(RDBMSStatement stmt);

}
