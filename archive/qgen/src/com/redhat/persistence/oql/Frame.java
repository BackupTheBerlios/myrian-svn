package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Frame
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/30 $
 **/

class Frame {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Frame.java#1 $ by $Author: rhs $, $DateTime: 2003/12/30 22:37:27 $";

    private Expression m_expr;
    private Frame m_parent;

    private ObjectType m_type = null;
    private boolean m_nullable = true;
    private boolean m_collection = true;
    private int m_correlationMax = Integer.MAX_VALUE;
    private int m_correlationMin = Integer.MIN_VALUE;
    private Set m_constrained = new HashSet();
    private Set m_injection = new HashSet();
    private Set m_keys = new HashSet();

    Frame(Expression expr, Frame parent) {
        m_expr = expr;
        m_parent = parent;
    }

    Expression getExpression() {
        return m_expr;
    }

    Frame getParent() {
        return m_parent;
    }

    void setType(ObjectType type) {
        m_type = type;
    }

    ObjectType getType() {
        return m_type;
    }

    void setNullable(boolean value) {
        m_nullable = value;
    }

    boolean isNullable() {
        return m_nullable;
    }

    void setCollection(boolean value) {
        m_collection = value;
    }

    boolean isCollection() {
        return m_collection;
    }

    void setCorrelationMax(int correlationMax) {
        m_correlationMax = correlationMax;
    }

    int getCorrelationMax() {
        return m_correlationMax;
    }

    void setCorrelationMin(int correlationMin) {
        m_correlationMin = correlationMin;
    }

    int getCorrelationMin() {
        return m_correlationMin;
    }

    Set getConstrained() {
        return m_constrained;
    }

    Set getInjection() {
        return m_injection;
    }

    void addKey(Collection key) {
        m_keys.add(Collections.unmodifiableList(new ArrayList(key)));
    }

    Set getKeys() {
        return m_keys;
    }

    void addAllKeys(Collection keys) {
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            addKey((Collection) it.next());
        }
    }

    boolean isKey(Collection key) {
        return m_keys.contains
            (Collections.unmodifiableList(new ArrayList(key)));
    }

    boolean isSet() {
        return !m_keys.isEmpty();
    }

}
