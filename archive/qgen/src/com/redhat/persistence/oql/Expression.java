package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/23 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Expression.java#3 $ by $Author: rhs $, $DateTime: 2004/01/23 15:34:30 $";

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

    abstract Code.Frame frame(Code code);

    abstract void emit(Code code);

    abstract String summary();

}
