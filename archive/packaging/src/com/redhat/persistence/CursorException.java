package com.redhat.persistence;

/**
 * CursorException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class CursorException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/CursorException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private Cursor m_cursor;

    CursorException(Cursor c, String msg) {
	super(msg);
	m_cursor = c;
    }

    public Cursor getCursor() {
	return m_cursor;
    }

}
