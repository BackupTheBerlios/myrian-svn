package com.arsdigita.persistence.proto.engine.rdbms;

/**
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/14 $
 **/

abstract class Join {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Join.java#2 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    abstract void write(SQLWriter w);

}
