package com.arsdigita.persistence.proto;

/**
 * NoRowException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public class NoRowException extends CursorException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/NoRowException.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 15:25:34 $";

    NoRowException(Cursor c) {
	super(c, "cursor is not currently on a row");
    }

}
