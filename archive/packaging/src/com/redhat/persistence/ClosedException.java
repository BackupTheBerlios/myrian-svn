package com.redhat.persistence;

/**
 * ClosedException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class ClosedException extends CursorException {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/ClosedException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    ClosedException(Cursor c) {
	super(c, "cursor closed");
    }

}
