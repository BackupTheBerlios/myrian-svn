package com.arsdigita.persistence.proto;

/**
 * ClosedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public class ClosedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ClosedException.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 15:25:34 $";

    ClosedException(Cursor c) {
	super(c, "cursor closed");
    }

}
