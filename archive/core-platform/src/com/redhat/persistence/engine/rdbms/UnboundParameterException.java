package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.*;

/**
 * UnboundParameterException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class UnboundParameterException extends RDBMSException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/UnboundParameterException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private Path m_parameter;

    UnboundParameterException(Path parameter) {
	super("unbound parameter: " + parameter);
	m_parameter = parameter;
    }

    public Path getParameter() {
	return m_parameter;
    }

}
