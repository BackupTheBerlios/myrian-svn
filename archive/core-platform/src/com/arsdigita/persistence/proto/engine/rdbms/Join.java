package com.arsdigita.persistence.proto.engine.rdbms;

/**
 * Join
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

abstract class Join {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/Join.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    abstract void write(SQLWriter w);

    public String toString() {
        SQLWriter w = new UnboundWriter();
        w.write(this);
        return w.getSQL();
    }

}
