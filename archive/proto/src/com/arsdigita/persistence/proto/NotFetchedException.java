package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * NotFetchedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public class NotFetchedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/NotFetchedException.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 15:25:34 $";

    private Path m_path;

    public NotFetchedException(Cursor c, Path p) {
	super(c, "cursor does not not fetch path: " + p);
	m_path = p;
    }

    public Path getPath() {
	return m_path;
    }

}
