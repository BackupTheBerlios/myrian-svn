package com.arsdigita.persistence.proto;

/**
 * NoRowException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class NoRowException extends CursorException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/NoRowException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    NoRowException(Cursor c) {
	super(c, "cursor is not currently on a row");
    }

}
