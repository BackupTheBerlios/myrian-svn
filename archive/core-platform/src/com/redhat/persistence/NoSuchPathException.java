package com.redhat.persistence;

import com.redhat.persistence.common.*;

/**
 * NoSuchPathException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class NoSuchPathException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/NoSuchPathException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private Path m_path;

    public NoSuchPathException(Path path) {
	super("no such path: " + path);
	m_path = path;
    }

    public Path getPath() {
	return m_path;
    }

}
