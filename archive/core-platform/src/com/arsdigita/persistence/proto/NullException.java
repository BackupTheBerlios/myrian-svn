package com.arsdigita.persistence.proto;

/**
 * NullException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class NullException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/NullException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private Role m_role;

    NullException(Role role) {
	m_role = role;
    }

    public Role getRole() {
	return m_role;
    }

}
