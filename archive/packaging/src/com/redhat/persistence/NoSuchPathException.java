package com.redhat.persistence;

import com.redhat.persistence.common.*;

/**
 * NoSuchPathException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class NoSuchPathException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/NoSuchPathException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private Path m_path;

    public NoSuchPathException(Path path) {
	super("no such path: " + path);
	m_path = path;
    }

    public Path getPath() {
	return m_path;
    }

}
