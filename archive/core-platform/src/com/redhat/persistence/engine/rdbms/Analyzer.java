/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;

import java.util.*;

/**
 * Analyzer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/15 $
 **/

class Analyzer {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/engine/rdbms/Analyzer.java#2 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    private Expression m_expr;
    private Map m_canonical = new HashMap();

    public Analyzer(Expression expr) {
        m_expr = expr;
        if (m_expr != null) {
            m_expr.dispatch(m_esw);
        }
    }

    public boolean isDuplicate(Path path) {
        return m_canonical.containsKey(path);
    }

    public Path getCanonical(Path path) {
        return (Path) m_canonical.get(path);
    }

    public Map getAssertions() {
        return m_canonical;
    }

    private Expression.Switch m_esw = new Expression.Switch() {
        public void onQuery(Query query) {}
        public void onCondition(Condition c) {
            c.dispatch(m_csw);
        }
        public void onVariable(Expression.Variable v) {}
        public void onValue(Expression.Value v) {}
        public void onPassthrough(Expression.Passthrough p) {}
    };

    private Condition.Switch m_csw = new Condition.Switch() {
        public void onAnd(Condition.And and) {
            Analyzer left = new Analyzer(and.getLeft());
            Analyzer right = new Analyzer(and.getRight());
            m_canonical.putAll(left.m_canonical);
            m_canonical.putAll(right.m_canonical);
        }
        public void onOr(Condition.Or or) {
            Analyzer left = new Analyzer(or.getLeft());
            Analyzer right = new Analyzer(or.getRight());
            m_canonical.putAll(left.m_canonical);
            m_canonical.keySet().retainAll(right.m_canonical.keySet());
        }
        public void onNot(Condition.Not not) {
            // should really propogate negative asserts here so we can
            // catch not not equals (double negatives)
        }
        public void onIn(Condition.In in) {
            // in isn't implemented with a join, so we ignore this
            // right now
        }

        private void equality(Expression l, Expression r) {
            if (!(l instanceof Expression.Variable) ||
                !(r instanceof Expression.Variable)) {
                return;
            }

            Path left = ((Expression.Variable) l).getPath();
            Path right = ((Expression.Variable) r).getPath();
            if (left == null) {
                m_canonical.put(right, left);
            } else if (right == null) {
                m_canonical.put(left, right);
            } else if (left.getPath().compareTo(right.getPath()) > 0) {
                m_canonical.put(left, right);
            } else {
                m_canonical.put(right, left);
            }
        }

        public void onEquals(Condition.Equals eq) {
            equality(eq.getLeft(), eq.getRight());
        }
        public void onContains(Condition.Contains c) {
            equality(c.getLeft(), c.getRight());
        }
    };

}
