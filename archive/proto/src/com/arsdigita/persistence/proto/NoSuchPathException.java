package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * NoSuchPathException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public class NoSuchPathException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/NoSuchPathException.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 17:02:22 $";

    private Path m_path;

    public NoSuchPathException(Path path) {
	super("no such path: " + path);
	m_path = path;
    }

    public Path getPath() {
	return m_path;
    }

}
