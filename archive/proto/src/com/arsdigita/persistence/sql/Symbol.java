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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Symbol
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class Symbol extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/sql/Symbol.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private static Map s_hashMap = new HashMap();

    private String m_text;

    private List m_leafElements;

    private Symbol(String text) {
        m_text = text;
    }

    public String getText() {
        return m_text;
    }

    public boolean isLeaf() {
        return true;
    }

    public List getLeafElements() {
        if (m_leafElements == null) {
            m_leafElements = new ArrayList();
            addLeafElements(m_leafElements);
        }
        return m_leafElements;
    }

    public void addLeafElements(List l) {
        l.add(this);
    }

    void makeString(SQLWriter result, Transformer tran) {
        if (m_text.equalsIgnoreCase("and")) {
            result.println();
        }

        if (Character.isJavaIdentifierStart(m_text.charAt(0))) {
            result.printID(m_text);
            result.print(' ');
        } else {
            result.print(m_text);
        }

        if (m_text.equals("(")) {
            result.pushIndent(result.getColumn());
        } else if (m_text.equals(")")) {
            result.popIndent();
        } else if (m_text.equals(",")) {
            result.println();
        }
    }

    public static Symbol getInstance(String text) {
        Symbol returnValue = (Symbol) s_hashMap.get(text);
        if (returnValue == null) {
            returnValue = new Symbol(text);
            s_hashMap.put(text, returnValue);
        }
        return returnValue;
    }

    public void traverse(Visitor v) {
        v.visit(this);
    }

}
