package com.redhat.persistence;

import com.redhat.persistence.common.*;

/**
 * NoSuchPathException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class NoSuchPathException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/NoSuchPathException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Path m_path;

    public NoSuchPathException(Path path) {
	super("no such path: " + path);
	m_path = path;
    }

    public Path getPath() {
	return m_path;
    }

}
