package com.arsdigita.persistence.proto;

/**
 * FlushException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class FlushException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/FlushException.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    FlushException(String msg) {
	super(msg, false);
    }

}
