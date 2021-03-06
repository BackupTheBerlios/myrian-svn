package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.*;

/**
 * UnboundParameterException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class UnboundParameterException extends RDBMSException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/UnboundParameterException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Path m_parameter;

    UnboundParameterException(Path parameter) {
	super("unbound parameter: " + parameter);
	m_parameter = parameter;
    }

    public Path getParameter() {
	return m_parameter;
    }

}
