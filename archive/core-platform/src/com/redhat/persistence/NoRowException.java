package com.redhat.persistence;

/**
 * NoRowException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class NoRowException extends CursorException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/NoRowException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    NoRowException(Cursor c) {
	super(c, "cursor is not currently on a row");
    }

}
