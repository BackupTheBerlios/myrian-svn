package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.EngineException;

/**
 * RDBMSException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public abstract class RDBMSException extends EngineException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/RDBMSException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    protected RDBMSException(String message) {
	super(message);
    }

}
