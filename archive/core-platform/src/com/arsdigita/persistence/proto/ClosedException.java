package com.arsdigita.persistence.proto;

/**
 * ClosedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class ClosedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/ClosedException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    ClosedException(Cursor c) {
	super(c, "cursor closed");
    }

}
