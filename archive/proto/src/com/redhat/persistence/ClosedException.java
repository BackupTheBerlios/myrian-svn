package com.redhat.persistence;

/**
 * ClosedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class ClosedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/ClosedException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    ClosedException(Cursor c) {
	super(c, "cursor closed");
    }

}
