package com.redhat.persistence;

/**
 * CursorException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class CursorException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/CursorException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Cursor m_cursor;

    CursorException(Cursor c, String msg) {
	super(msg);
	m_cursor = c;
    }

    public Cursor getCursor() {
	return m_cursor;
    }

}
