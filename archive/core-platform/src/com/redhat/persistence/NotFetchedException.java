package com.redhat.persistence;

import com.redhat.persistence.common.*;

/**
 * NotFetchedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class NotFetchedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/NotFetchedException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private Path m_path;

    public NotFetchedException(Cursor c, Path p) {
	super(c, "cursor does not fetch path: " + p);
	m_path = p;
    }

    public Path getPath() {
	return m_path;
    }

}
