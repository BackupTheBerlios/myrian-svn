package com.redhat.persistence;

/**
 * NoRowException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class NoRowException extends CursorException {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/NoRowException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    NoRowException(Cursor c) {
	super(c, "cursor is not currently on a row");
    }

}
