package com.redhat.persistence;

/**
 * NoRowException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class NoRowException extends CursorException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/NoRowException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    NoRowException(Cursor c) {
	super(c, "cursor is not currently on a row");
    }

}
