package com.redhat.persistence;

/**
 * CursorException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class CursorException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/CursorException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private Cursor m_cursor;

    CursorException(Cursor c, String msg) {
	super(msg);
	m_cursor = c;
    }

    public Cursor getCursor() {
	return m_cursor;
    }

}
