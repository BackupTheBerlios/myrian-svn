package com.arsdigita.persistence.sql;

import java.util.*;

/**
 * SQL
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class SQL extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/SQL.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private List m_elements = new ArrayList();
    private List m_elementsNoMod = Collections.unmodifiableList(m_elements);

    // Cache the results of makeString. This cached variable is
    // flushed whenever we modify this object.
    private String m_textString;

    public SQL() {}

    public void addElement(Element el) {
        m_elements.add(el);
        flushCache();
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
        flushCache();
    }

    String makeString() {
        if (m_textString == null) {
            StringBuffer result = new StringBuffer();
            Element el;
            for (Iterator it = getElements(); it.hasNext(); ) {
                el = (Element) it.next();
                result.append(el);
                if (it.hasNext()) {
                    result.append(" ");
                }
            }
            m_textString = result.toString();
        }

        return m_textString;
    }

    private void flushCache() {
        m_textString = null;
    }
}
