package com.redhat.persistence.engine.rdbms;

/**
 * StatementLifecycle
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public interface StatementLifecycle {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/StatementLifecycle.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    void beginPrepare();
    void endPrepare();

    void beginSet(int pos, int type, Object obj);
    void endSet();

    void beginExecute();
    void endExecute(int updateCount);

    void beginNext();
    void endNext(boolean more);

    void beginGet(String column);
    void endGet(Object result);

    void beginClose();
    void endClose();

}
