package com.arsdigita.persistence.proto;

/**
 * Cursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/25 $
 **/

abstract class Cursor {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Cursor.java#1 $ by $Author: rhs $, $DateTime: 2002/11/25 19:30:13 $";

    public abstract boolean next();

    public abstract Object getValue();

}
