package com.arsdigita.persistence.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Symbol
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class Symbol extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Symbol.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static Map s_hashMap = new HashMap();

    private String m_text;

    private List m_leafElements;

    private Symbol(String text) {
        m_text = text;
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

    String makeString() {
        return m_text;
    }


    public static Symbol getInstance(String text) {
        Symbol returnValue = (Symbol) s_hashMap.get(text);
        if (returnValue == null) {
            returnValue = new Symbol(text);
            s_hashMap.put(text, returnValue);
        }
        return returnValue;
    }
}
