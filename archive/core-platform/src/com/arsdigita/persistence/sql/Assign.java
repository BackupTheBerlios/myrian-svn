/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence.sql;

import java.util.List;

/**
 * Assign
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/11/14 $
 **/

public class Assign extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Assign.java#5 $ by $Author: rhs $, $DateTime: 2002/11/14 18:09:55 $";

    private Identifier m_lhs;
    private SQL m_rhs;

    public Assign(Identifier lhs, SQL rhs) {
        m_lhs = lhs;
        m_rhs = rhs;
    }

    public Identifier getLHS() {
        return m_lhs;
    }

    public SQL getRHS() {
        return m_rhs;
    }

    public boolean isLeaf() {
        return false;
    }

    public void addLeafElements(List l) {
        m_lhs.addLeafElements(l);
        l.add(Symbol.getInstance("="));
        m_rhs.addLeafElements(l);
    }

    void makeString(SQLWriter result, Transformer tran) {
        m_lhs.output(result, tran);
        result.print(" = ");
        m_rhs.output(result, tran);
    }

    public void traverse(Visitor v) {
        v.visit(this);
        m_lhs.traverse(v);
        m_rhs.traverse(v);
    }

}
