package com.arsdigita.persistence.proto;

/**
 * CursorException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class CursorException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/CursorException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private Cursor m_cursor;

    CursorException(Cursor c, String msg) {
	super(msg);
	m_cursor = c;
    }

    public Cursor getCursor() {
	return m_cursor;
    }

}
