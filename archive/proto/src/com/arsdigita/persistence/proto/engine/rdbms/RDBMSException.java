package com.arsdigita.persistence.proto.engine.rdbms;

/**
 * RDBMSException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public abstract class RDBMSException extends RuntimeException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSException.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 09:30:02 $";

    protected RDBMSException(String message) {
	super(message);
    }

}
