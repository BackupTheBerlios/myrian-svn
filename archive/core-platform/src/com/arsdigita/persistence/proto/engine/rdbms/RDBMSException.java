package com.arsdigita.persistence.proto.engine.rdbms;

/**
 * RDBMSException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public abstract class RDBMSException extends RuntimeException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    protected RDBMSException(String message) {
	super(message);
    }

}
