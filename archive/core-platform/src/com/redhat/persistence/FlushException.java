package com.redhat.persistence;

/**
 * FlushException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class FlushException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/FlushException.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    FlushException(String msg) {
	super(msg, false);
    }

}
