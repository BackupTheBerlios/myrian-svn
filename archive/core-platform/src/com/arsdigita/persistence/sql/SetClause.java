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

import java.util.*;

/**
 * SetClause
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 **/

public class SetClause extends Clause {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/SetClause.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private List m_assigns = new ArrayList();
    private List m_assignsNoMod = Collections.unmodifiableList(m_assigns);

    public SetClause() {}

    public void addAssign(Assign assign) {
        m_assigns.add(assign);
    }

    public Iterator getAssigns() {
        return m_assignsNoMod.iterator();
    }

    public void addLeafElements(List l) {
        l.add(Symbol.getInstance("set"));
        for (Iterator it = getAssigns(); it.hasNext(); ) {
            Assign assign = (Assign) it.next();
            assign.addLeafElements(l);
            if (it.hasNext()) {
                l.add(Symbol.getInstance(","));
            }
        }
    }

    void makeString(SQLWriter result, Transformer tran) {
        result.print("set ");

        result.pushIndent(result.getColumn());
        for (Iterator it = getAssigns(); it.hasNext(); ) {
            Element el = (Element) it.next();
            el.output(result, tran);
            if (it.hasNext()) {
                result.println(",");
            }
        }
        result.popIndent();
    }
}
