package com.arsdigita.persistence.proto;

/**
 * FlushException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public class FlushException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/FlushException.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 09:30:02 $";

    FlushException(String msg) {
	super(msg, false);
    }

}
