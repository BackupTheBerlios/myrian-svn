package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * NotFetchedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class NotFetchedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/NotFetchedException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private Path m_path;

    public NotFetchedException(Cursor c, Path p) {
	super(c, "cursor does not fetch path: " + p);
	m_path = p;
    }

    public Path getPath() {
	return m_path;
    }

}
