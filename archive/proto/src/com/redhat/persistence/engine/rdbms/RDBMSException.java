package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.EngineException;

/**
 * RDBMSException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public abstract class RDBMSException extends EngineException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/RDBMSException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    protected RDBMSException(String message) {
	super(message);
    }

}
