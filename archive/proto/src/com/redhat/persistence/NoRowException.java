package com.redhat.persistence;

/**
 * NoRowException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class NoRowException extends CursorException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/NoRowException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    NoRowException(Cursor c) {
	super(c, "cursor is not currently on a row");
    }

}
