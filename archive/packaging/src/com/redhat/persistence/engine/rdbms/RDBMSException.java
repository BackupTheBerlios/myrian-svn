package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.EngineException;

/**
 * RDBMSException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public abstract class RDBMSException extends EngineException {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/engine/rdbms/RDBMSException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    protected RDBMSException(String message) {
	super(message);
    }

}
