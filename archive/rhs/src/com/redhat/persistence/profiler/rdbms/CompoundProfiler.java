/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.profiler.rdbms;

import com.redhat.persistence.engine.rdbms.*;

import java.sql.*;
import java.util.*;

/**
 * CompoundProfiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/04/05 $
 **/

public class CompoundProfiler implements RDBMSProfiler {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/profiler/rdbms/CompoundProfiler.java#1 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    private List m_children = new ArrayList();

    public CompoundProfiler() {}

    public void add(RDBMSProfiler child) {
        m_children.add(child);
    }

    public StatementLifecycle getLifecycle(RDBMSStatement stmt) {
        CompoundLifecycle result = null;
        for (Iterator it = m_children.iterator(); it.hasNext(); ) {
            RDBMSProfiler child = (RDBMSProfiler) it.next();
            StatementLifecycle sl = child.getLifecycle(stmt);
            if (sl == null) { continue; }
            if (result == null) { result = new CompoundLifecycle(); }
            result.add(sl);
        }
        return result;
    }

    private static class CompoundLifecycle implements StatementLifecycle {

        private List m_children = new ArrayList();

        public void add(StatementLifecycle child) {
            m_children.add(child);
        }

        public void beginPrepare() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginPrepare();
            }
        }

        public void endPrepare() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endPrepare();
            }
        }

        public void endPrepare(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endPrepare(e);
            }
        }

        public void beginSet(int pos, int type, Object obj) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginSet(pos, type, obj);
            }
        }

        public void endSet() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endSet();
            }
        }

        public void endSet(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endSet(e);
            }
        }

        public void beginExecute() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginExecute();
            }
        }

        public void endExecute(int updateCount) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endExecute(updateCount);
            }
        }

        public void endExecute(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endExecute(e);
            }
        }

        public void beginNext() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginNext();
            }
        }

        public void endNext(boolean more) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endNext(more);
            }
        }

        public void endNext(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endNext(e);
            }
        }

        public void beginGet(String column) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginGet(column);
            }
        }

        public void endGet(Object result) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endGet(result);
            }
        }

        public void endGet(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endGet(e);
            }
        }

        public void beginClose() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.beginClose();
            }
        }

        public void endClose() {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endClose();
            }
        }

        public void endClose(SQLException e) {
            for (Iterator it = m_children.iterator(); it.hasNext(); ) {
                StatementLifecycle child = (StatementLifecycle) it.next();
                child.endClose(e);
            }
        }

    }

}
