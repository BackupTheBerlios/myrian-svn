package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/16 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Expression.java#2 $ by $Author: rhs $, $DateTime: 2004/01/16 16:27:01 $";

    private static Map KEYS = new HashMap();

    static synchronized Set getKeys(ObjectType type) {
        Set result = (Set) KEYS.get(type);
        if (result == null) {
            result = new HashSet();
            KEYS.put(type, result);
        }
        return result;
    }

    static synchronized void addKey(ObjectType type, Collection key) {
        getKeys(type).add(Collections.unmodifiableList(new ArrayList(key)));
    }

    static synchronized boolean isKey(ObjectType type, Collection key) {
        return getKeys(type).contains
            (Collections.unmodifiableList(new ArrayList(key)));
    }

    abstract void graph(Pane pane);

    abstract String summary();

}
