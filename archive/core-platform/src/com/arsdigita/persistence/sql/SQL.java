package com.arsdigita.persistence.sql;

import java.util.*;

/**
 * SQL
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 **/

public class SQL extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/SQL.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

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

}
