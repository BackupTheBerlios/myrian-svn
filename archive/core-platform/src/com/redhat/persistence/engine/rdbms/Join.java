package com.redhat.persistence.engine.rdbms;

/**
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

abstract class Join {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Join.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new UnboundWriter();
        w.write(this);
        return w.getSQL();
    }

}
