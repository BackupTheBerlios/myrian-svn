package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.EngineException;

/**
 * RDBMSException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/13 $
 **/

public abstract class RDBMSException extends EngineException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSException.java#2 $ by $Author: ashah $, $DateTime: 2003/05/13 16:11:19 $";

    protected RDBMSException(String message) {
	super(message);
    }

}
