package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.EngineException;

/**
 * RDBMSException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public abstract class RDBMSException extends EngineException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/RDBMSException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    protected RDBMSException(String message) {
	super(message);
    }

}
