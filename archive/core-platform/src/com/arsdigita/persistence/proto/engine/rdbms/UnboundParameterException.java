package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

/**
 * UnboundParameterException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class UnboundParameterException extends RDBMSException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/UnboundParameterException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private Path m_parameter;

    UnboundParameterException(Path parameter) {
	super("unbound parameter: " + parameter);
	m_parameter = parameter;
    }

    public Path getParameter() {
	return m_parameter;
    }

}
