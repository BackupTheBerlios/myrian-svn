package com.redhat.persistence;

/**
 * ClosedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class ClosedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/ClosedException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    ClosedException(Cursor c) {
	super(c, "cursor closed");
    }

}
