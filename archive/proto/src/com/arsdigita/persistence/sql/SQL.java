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
 * SQL
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class SQL extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/sql/SQL.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private List m_elements = new ArrayList();
    private List m_elementsNoMod = Collections.unmodifiableList(m_elements);

    public SQL() {}

    public void addElement(Element el) {
        m_elements.add(el);
        flush();
    }

    public Iterator getElements() {
        return m_elementsNoMod.iterator();
    }

    public boolean isLeaf() {
        return false;
    }

    public void addLeafElements(List l) {
        Element el;
        for (Iterator it = getElements(); it.hasNext(); ) {
            el = (Element) it.next();
            el.addLeafElements(l);
        }
        flush();
    }

    void makeString(SQLWriter result, Transformer tran) {
        Element el;
        for (Iterator it = getElements(); it.hasNext(); ) {
            el = (Element) it.next();
            el.output(result, tran);
        }
    }

    public void traverse(Visitor v) {
        v.visit(this);
        for (Iterator it = m_elements.iterator(); it.hasNext(); ) {
            ((Element) it.next()).traverse(v);
        }
    }

}
